package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.events.client.DetectConflictsEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.AgentService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Agency implements Runnable {

    private static final Object synchronizer = new Object();
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

        // Map for agent -> is done executing a plan
        HashMap<String, Boolean> agentIsFinished = new HashMap<>();
        agents.forEach(agent -> agentIsFinished.put(agent.getLabel(), true));

        HashMap<String, Lock> agentLocks = new HashMap<>();
        agents.forEach(agent -> agentLocks.put(agent.getLabel(), new ReentrantLock()));

        while ((nextIndependentGoals = GlobalLevelService.getInstance().getIndependentGoals()).size() > 0) {

            // Assign goals to the best agents and wait for plans to finish
            getBestAgents(nextIndependentGoals).entrySet().parallelStream().forEach(goalAgentEntry -> {

                Goal goal = goalAgentEntry.getKey();
                Agent bestAgent = goalAgentEntry.getValue();

                // Lock this agent
                agentLocks.get(bestAgent.getLabel()).lock();

                try {
                    agentIsFinished.put(bestAgent.getLabel(), false);

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
                }
                finally {
                    // unlock this agent
                    agentLocks.get(bestAgent.getLabel()).unlock();
                }
            });
        }

        try {
            // wait indefinitely until problem is solved
            synchronized (synchronizer) {
                synchronizer.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        System.err.println("Agency is exiting.");
    }

    /**
     * Get the best agents for all goals
     *
     * @return
     */
    private HashMap<Goal, Agent> getBestAgents(List<Goal> goals) {

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

    @Subscribe
    public void problemSolvedEventSubscriber(ProblemSolvedEvent event) {
        // wait for all threads to finish
        ThreadService.shutdown();

        // allow this thread to be joined
        synchronized (synchronizer) {
            synchronizer.notify();
        }
    }
}