package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.events.client.DetectConflictsEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.services.AgentService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

public class Agency implements Runnable {

    public Agency(Level level) {
        GlobalLevelService.getInstance().setLevel(level);
    }

    @Override
    public void run() {

        int numberOfAgents = GlobalLevelService.getInstance().getLevel().getAgents().size();

        AgentService.getInstance().addAgents(
                GlobalLevelService.getInstance().getLevel().getAgents()
        );

        GlobalLevelService.getInstance().getLevel().getAgents().forEach(agent -> {
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

                // Get the goal estimations and assign goals (blocks)
                String bestAgent = goalEstimationSubscriber.getBestAgent();

                System.err.println("Assigning goal " + goalEstimationSubscriber.getGoal().getLabel() + " to " + bestAgent);
                EventBusService.post(new GoalAssignmentEvent(bestAgent, goalEstimationSubscriber.getGoal()));
            }
        });
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
    }
}