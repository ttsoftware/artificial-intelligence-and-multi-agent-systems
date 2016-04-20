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

import java.util.List;

public class Agency implements Runnable {

    private static final Object synchronizer = new Object();

    @Override
    public void run() {
        List<Agent> agents = GlobalLevelService.getInstance().getLevel().getAgents();

        int numberOfAgents = agents.size();

        AgentService.getInstance().addAgents(agents);

        agents.forEach(agent -> {
            System.err.println(Thread.currentThread().getName() + ": Constructing agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            ThreadService.execute(new AgentThread());
        });

        // Register for self-handled events
        EventBusService.register(this);

        // Offer goals to agents
        // Each goalQueue is independent of one another so we can parallelStream
        GlobalLevelService.getInstance().getLevel().getGoalQueues().parallelStream().forEach(goalQueue -> {

            Goal goal;
            // we can poll(), since we know all estimations have finished
            while ((goal = goalQueue.poll()) != null) {

                // Register for incoming goal estimations
                GoalEstimationEventSubscriber goalEstimationSubscriber = new GoalEstimationEventSubscriber(goal, numberOfAgents);
                EventBusService.register(goalEstimationSubscriber);

                // offer the goal
                System.err.println("Offering goal: " + goal.getLabel());

                EventBusService.post(new GoalOfferEvent(goal));

                // Get the goal estimations (blocks current thread)
                Agent bestAgent = goalEstimationSubscriber.getBestAgent();

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
        });

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

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {
        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getAgent(), event.getPlan()));
    }

    @Subscribe
    public void detectConflictEventSubscriber(DetectConflictsEvent event) {

        // TODO Detect conflict in the plans at given timestep

        // Set to true if there is a conflict
        event.setResponse(false);
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