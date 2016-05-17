package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.*;
import dtu.agency.events.EstimationEvent;
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

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;

public class Agency implements Runnable {

    private boolean isGettingHelp;
    private int numberOfAgents;
    private List<Agent> agents;

    private final KeyLockManager lockManager = KeyLockManagers.newLock();

    @Override
    public void run() {
        agents = GlobalLevelService.getInstance().getLevel().getAgents();
        numberOfAgents = agents.size();
        isGettingHelp = false;

        AgentService.getInstance().addAgents(agents);

        agents.forEach(agent -> {
            //System.err.println(Thread.currentThread().getName() + ": Constructing agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            ThreadService.execute(new AgentThread(agent));
        });

        // Register for self-handled events
        EventBusService.register(this);

        // Clone the priority queues
        List<PriorityBlockingQueue<Goal>> goalQueuesClone = GlobalLevelService.getInstance().getPriorityQueuesClone();
        while (!GlobalLevelService.getInstance().isAllGoalsSolved()) {

            // try solving the goals
            List<Goal> nextIndependentGoals;
            while ((nextIndependentGoals = GlobalLevelService.getInstance().getIndependentGoals()).size() > 0) {

                // create goal offer stream
                Stream<Map.Entry<Goal, Agent>> goalStream =  offerGoals(nextIndependentGoals)
                        .entrySet()
                        .stream()
                        .sorted(new Comparator<Map.Entry<Goal, Agent>>() {
                            @Override
                            public int compare(Map.Entry<Goal, Agent> entryA, Map.Entry<Goal, Agent> entryB) {
                                return entryA.getKey().getEstimatedSteps() - entryB.getKey().getEstimatedSteps();
                            }
                        });

                if (numberOfAgents > 1) {
                    // Parallelizing goal assigning if more than 1 agent
                    goalStream = goalStream.parallel();
                }

                // Assign goals to the best agents and wait for plans to finish
                goalStream.forEach(goalAgentEntry -> {

                    Goal goal = goalAgentEntry.getKey();
                    Agent bestAgent = goalAgentEntry.getValue();

                    // Lock this agent
                    lockManager.executeLocked(bestAgent.getNumber(), () -> {

                        // Assign this goal, and wait for response
                        //System.err.println("Assigning goal " + goal.getLabel() + " to " + bestAgent);

                        GoalAssignmentEvent goalAssignmentEvent = new GoalAssignmentEvent(bestAgent, goal);
                        EventBusService.post(goalAssignmentEvent);

                        // get the response containing the plan (blocks current thread)
                        // how long do we wish to wait for the agents to finish planning?
                        // right now we wait 2^32-1 milliseconds
                        ConcretePlan plan = goalAssignmentEvent.getResponse();

                        //System.err.println("Received offer for " + goal.getLabel() + " from " + bestAgent);

                        SendServerActionsEvent sendActionsEvent = new SendServerActionsEvent(goalAssignmentEvent.getAgent(), plan);
                        EventBusService.post(sendActionsEvent);

                        // wait for the plan to finish executing
                        boolean isFinished = sendActionsEvent.getResponse();

                        // We need to check if the goal has actually been solved
                        Position goalPosition = GlobalLevelService.getInstance().getPosition(goal);
                        BoardObject objectAtGoalPosition = GlobalLevelService.getInstance().getObject(goalPosition);

                        switch (objectAtGoalPosition.getType()) {
                            case GOAL:
                                // We need to re-assign goal task
                                lockManager.executeLocked(bestAgent.getNumber() + 1000, () -> {
                                    //System.err.println("We must re-offer: " + goal);
                                });
                                break;
                            case BOX_GOAL:
                                if (((BoxAndGoal) objectAtGoalPosition).isSolved()) {
                                    //System.err.println("The plan for goal: " + goal + " finished.");
                                    // this goal completed, so we can remove it from it's queue
                                    GlobalLevelService.getInstance().removeGoalFromQueue(goal);
                                }
                                else {
                                    lockManager.executeLocked(bestAgent.getNumber() + 1000, () -> {
                                        //System.err.println("We must re-offer: " + goal);
                                    });
                                }
                                break;
                            case AGENT_GOAL:
                                lockManager.executeLocked(bestAgent.getNumber() + 1000, () -> {
                                    //System.err.println("We must re-offer: " + goal);
                                });
                                break;
                            default:
                                //System.err.println("The plan for goal: " + goal + " finished.");
                                // this goal completed, so we can remove it from it's queue
                                GlobalLevelService.getInstance().removeGoalFromQueue(goal);
                                break;
                        }
                        return;
                    });
                });
            }

            //System.err.println("You are wrong and you should feel wrong.");

            // we need to try again from the beginning
            GlobalLevelService.getInstance().updatePriorityQueues(goalQueuesClone);
            goalQueuesClone = GlobalLevelService.getInstance().getPriorityQueuesClone();
        }

        // We should have solved the entire problem now
        EventBusService.post(new ProblemSolvedEvent());
        //System.err.println("Agency is exiting.");
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
            GoalEstimationEventSubscriber goalEstimationSubscriber = new GoalEstimationEventSubscriber(goal, agents);
            EventBusService.register(goalEstimationSubscriber);

            // offer the goal
            //System.err.println("Offering goal: " + goal.getLabel());

            EventBusService.post(new GoalOfferEvent(goal));

            goal.setEstimatedSteps(
                    goalEstimationSubscriber
                            .getEstimations()
                            .stream()
                            .mapToInt(EstimationEvent::getSteps)
                            .min()
                            .getAsInt()
            );

            // Get the goal estimations (blocks current thread)
            Agent bestAgent = goalEstimationSubscriber.getBestAgent();

            bestAgents.put(goal, bestAgent);
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
        //System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

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

        List<Agent> agentsWithoutVictim = new ArrayList<>(agents);
        agentsWithoutVictim.remove(event.getAgent());
        // the number of bad paths we wait for before we give up
        int maximumBadAgents = agentsWithoutVictim.size();

        if (maximumBadAgents == 0) {
            // no one can help us
            LinkedList<Position> failedPath = new LinkedList<>();
            List<LinkedList<Position>> failedPaths = new ArrayList<>();
            failedPaths.add(failedPath);
            event.setResponse(failedPaths);
        } else {

            // Subscribe to move obstacle estimations
            MoveObstacleEstimationEventSubscriber obstacleEstimationSubscriber = new MoveObstacleEstimationEventSubscriber(
                    event.getObstacle(),
                    agentsWithoutVictim
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
                if (estimation.isSolvedObstacle()) {
                    // an agent can move the obstacle! Wohoo!
                    break;
                }
                if (estimation.getPath().isEmpty()) {
                    // this agent cannot move this box
                    maximumBadAgents--;
                } else {
                    // this agent has an obstacle in its path for solving our obstacle
                    badAgentPaths.add(estimation.getPath());
                }
            }

            if (badAgentPaths.size() == maximumBadAgents) {
                // no agents can move our obstacle without help
                event.setResponse(badAgentPaths);
            } else {

                final Agent estimationAgent = estimation.getAgent();

                //System.err.println(estimationAgent + " is supposed to help move " + event.getObstacle());

                // Lock the helping agent - wait for him to become available
                lockManager.executeLocked(estimationAgent.getNumber(), () -> {
                    lockManager.executeLocked(estimationAgent.getNumber() + 1000, () -> {
                        // Assign the task of moving the obstacle to the best agent
                        MoveObstacleAssignmentEvent moveObstacleAssignmentEvent = new MoveObstacleAssignmentEvent(
                                estimationAgent,
                                event.getPath(),
                                event.getObstacle()
                        );

                        EventBusService.post(moveObstacleAssignmentEvent);

                        // Get the plan from the assigned agent
                        ConcretePlan plan = moveObstacleAssignmentEvent.getResponse();

                        // Send the plan to the client
                        SendServerActionsEvent sendActionsEvent = new SendServerActionsEvent(
                                estimationAgent,
                                plan
                        );
                        EventBusService.post(sendActionsEvent);

                        lockManager.executeLocked(event.getAgent().getNumber() + 1000, () -> {
                            // Allow the agent who is waiting for the obstacle to continue
                            event.setResponse(new ArrayList<>());

                            // wait for the plan to finish executing
                            boolean isFinished = sendActionsEvent.getResponse();

                            //System.err.println("The plan for moving obstacle " + event.getObstacle().getLabel() + " finished.");
                        });
                    });
                });
            }
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