package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.AgentService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

import java.util.HashMap;
import java.util.List;

public class Agency implements Runnable {

    private int numberOfAgents;

    @Override
    public void run() {
        List<Agent> agents = GlobalLevelService.getInstance().getLevel().getAgents();

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

                    System.err.println("The plan for goal: " + goal + " finished.");
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

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {
        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getAgent(), event.getPlan()));
    }

    /**
     * We re-post dead events until someone responds to them
     * Maybe we should only re-post a certain number of times?
     * @param event
     */
    @Subscribe
    public void deadEventSubscriber(DeadEvent event) {
        EventBusService.post(event.getEvent());
    }
}