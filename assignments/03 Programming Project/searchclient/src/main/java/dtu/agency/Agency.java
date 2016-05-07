package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.*;
import dtu.agency.events.agency.*;
import dtu.agency.events.agent.HelpMoveObstacleEvent;
import dtu.agency.events.agent.MoveObstacleEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.AgentService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class Agency implements Runnable {

    private int numberOfAgents;
    private List<Agent> agents;

    @Override
    public void run() {
        agents = GlobalLevelService.getInstance().getLevel().getAgents();

        numberOfAgents = agents.size();

        AgentService.getInstance().addAgents(agents);

        agents.forEach(agent -> {
            System.err.println(Thread.currentThread().getName() + ": Constructing agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            ThreadService.execute(new AgentThread());
        });

        // Register for self-handled events
        EventBusService.register(this);

        List<Goal> nextIndependentGoals;

        final KeyLockManager lockManager = KeyLockManagers.newLock();

        while ((nextIndependentGoals = GlobalLevelService.getInstance().getIndependentGoals()).size() > 0) {

            // Assign goals to the best agents and wait for plans to finish
            offerGoals(nextIndependentGoals).entrySet().parallelStream().forEach(goalAgentEntry -> {

                Goal goal = goalAgentEntry.getKey();
                Agent bestAgent = goalAgentEntry.getValue();

                // Lock this agent
                lockManager.executeLocked(bestAgent.getNumber(), () -> {

                    // Assign this goal, and wait for response
                    System.err.println("Assigning goal " + goal.getLabel() + " to " + bestAgent);

                    GoalAssignmentEvent goalAssignmentEvent = new GoalAssignmentEvent(bestAgent, goal);
                    EventBusService.post(goalAssignmentEvent);

                    // get the response containing the plan (blocks current thread)
                    // how long do we wish to wait for the agents to finish planning?
                    // right now we wait 2^32-1 milliseconds
                    ConcretePlan plan = goalAssignmentEvent.getResponse();

                    System.err.println("Received offer for " + goal.getLabel() + " from " + bestAgent);

                    SendServerActionsEvent sendActionsEvent = new SendServerActionsEvent(goalAssignmentEvent.getAgent(), plan);
                    EventBusService.post(sendActionsEvent);

                    // wait for the plan to finish executing
                    boolean isFinished = sendActionsEvent.getResponse();

                    // We need to check if the goal has actually been solve
                    Position goalPosition = GlobalLevelService.getInstance().getPosition(goal);
                    BoardObject objectAtGoalPosition = GlobalLevelService.getInstance().getObject(goalPosition);

                    switch (objectAtGoalPosition.getType()) {
                        case GOAL:
                            // We need to re-assign goal task
                            break;
                        case BOX_GOAL:
                            if (!((BoxAndGoal) objectAtGoalPosition).isSolved()) {
                                // We need to re-assign goal task
                            }
                            break;
                        default:
                            System.err.println("The plan for goal: " + goal + " finished.");
                            // this goal completed, so we can remove it from it's queue
                            GlobalLevelService.getInstance().removeGoalFromQueue(goal);
                            break;
                    }
                    return;
                });
            });
        }

        // We should have solved the entire problem now
        EventBusService.post(new ProblemSolvedEvent());

        System.err.println("Agency is exiting.");
    }

    /**
     * Get the best agents for all goals
     *
     * @return
     */
    private HashMap<Goal, Agent> offerGoals(List<Goal> goals) {

        HashMap<Goal, Agent> bestAgents = new HashMap<>();

        // Offer goals to agents
        goals.forEach(goal -> {

            // Register for incoming goal estimations
            GoalEstimationEventSubscriber goalEstimationSubscriber = new GoalEstimationEventSubscriber(goal, numberOfAgents);
            EventBusService.register(goalEstimationSubscriber);

            // offer the goal
            System.err.println("Offering goal: " + goal.getLabel());

            EventBusService.post(new GoalOfferEvent(goal));

            // Get the goal estimations (blocks current thread)
            bestAgents.put(goal, goalEstimationSubscriber.getBestAgent());
        });

        return bestAgents;
    }

    /**
     * Offer plans to the PlannerClient
     *
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {
        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getAgent(), event.getPlan()));
    }

    /**
     * An agent needs help moving an obstacle
     * We first ask other agents for estimations, subsequently assign them the task of moving the obstacle
     *
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void helpMoveObstacleEventSubscriber(HelpMoveObstacleEvent event) {

        // Subscribe to move obstacle estimations
        MoveObstacleEstimationEventSubscriber obstacleEstimationSubscriber = new MoveObstacleEstimationEventSubscriber(
                event.getObstacle(),
                numberOfAgents
        );

        EventBusService.register(obstacleEstimationSubscriber);

        // Ask agents to bid for obstacle
        EventBusService.post(new MoveObstacleOfferEvent(
                event.getPath(),
                event.getObstacle()
        ));

        // Get the move obstacle estimations (blocks current thread)
        PriorityBlockingQueue<MoveObstacleEstimationEvent> agentEstimations
                = obstacleEstimationSubscriber.getEstimations();

        // loop through all estimations, insert bad ones in list for calling agent
        List<LinkedList<Position>> badAgentPaths = new ArrayList<>();
        MoveObstacleEstimationEvent estimation;
        while ((estimation = agentEstimations.poll()) != null) {
            if (estimation.isSolvesObstacle()) {
                // an agent can move the obstacle! Wohoo!
                break;
            }
            // this agent has an obstacle in its path for solving our obstacle
            badAgentPaths.add(estimation.getPath());
        }

        if (badAgentPaths.size() == numberOfAgents) {
            // no agents can move our obstacle without help
            event.setResponse(badAgentPaths);
        } else {
            // Assign the task of moving the obstacle to the best agent
            MoveObstacleAssignmentEvent moveObstacleAssignmentEvent = new MoveObstacleAssignmentEvent(
                    estimation.getAgent(),
                    event.getPath(),
                    event.getObstacle()
            );

            EventBusService.post(moveObstacleAssignmentEvent);

            // Get the plan from the assigned agent
            ConcretePlan plan = moveObstacleAssignmentEvent.getResponse();

            // Send the plan to the client
            SendServerActionsEvent sendActionsEvent = new SendServerActionsEvent(
                    estimation.getAgent(),
                    plan
            );
            EventBusService.post(sendActionsEvent);

            // wait for the plan to finish executing
            boolean isFinished = sendActionsEvent.getResponse();

            System.err.println("The plan for moving obstacle " + event.getObstacle().getLabel() + " finished.");

            // Allow the agent who is waiting for the obstacle to continue
            event.setResponse(new ArrayList<>());
        }
    }

    /**
     * We re-post dead events until someone responds to them
     * Maybe we should only re-post a certain number of times?
     *
     * @param event
     */
    @Subscribe
    public void deadEventSubscriber(DeadEvent event) {
        EventBusService.post(event.getEvent());
    }
}