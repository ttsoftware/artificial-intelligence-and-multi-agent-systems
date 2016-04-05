package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Level;
import dtu.agency.events.SendServerActionsEvent;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.services.EventBusService;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;

public class Agency implements Runnable {

    public Agency(Level level) {
        LevelService.getInstance().setLevel(level);
    }

    @Override
    public void run() {

        List<String> agentLabels = new ArrayList<>();

        LevelService.getInstance().getLevel().getAgents().forEach(agent -> {
            System.err.println("Starting agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            EventBusService.execute(new AgentThread(agent));

            agentLabels.add(agent.getLabel());
        });

        // Register for self-handled events
        EventBusService.register(this);

        List<GoalEstimationEventSubscriber> goalEstimationSubscribers = new ArrayList<>();

        // Offer goals to agents
        LevelService.getInstance().getLevel().getGoalQueue().forEach(goal -> {

            // Register for incoming goal estimations
            GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(goal, agentLabels.size());
            EventBusService.register(goalEstimationEventSubscriber);

            // store the subscriber
            goalEstimationSubscribers.add(goalEstimationEventSubscriber);

            System.err.println("Offering goal: " + goal.getLabel());
            EventBusService.post(new GoalOfferEvent(goal));
        });

        // Get the goal estimations and assign goals
        for (GoalEstimationEventSubscriber subscriber : goalEstimationSubscribers) {
            String bestAgent = subscriber.getBestAgent();

            System.err.println("Assigning goal " + subscriber.getGoal().getLabel() + " to " + bestAgent);
            EventBusService.post(new GoalAssignmentEvent(bestAgent, subscriber.getGoal()));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {

        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getPlan().getActions()));
    }

    @Subscribe
    public void problemSolvedEventSubscriber(ProblemSolvedEvent event) {
        // wait for all threads to finish
        EventBusService.getThreads().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });
    }
}