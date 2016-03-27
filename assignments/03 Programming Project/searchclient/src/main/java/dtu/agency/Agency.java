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
import dtu.agency.planners.ConcretePlan;
import dtu.agency.services.EventBusService;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Agency implements Runnable {

    private Hashtable<String, ConcretePlan> currentPlans = new Hashtable<>();

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
            GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(goal, agentLabels);
            EventBusService.register(goalEstimationEventSubscriber);
            goalEstimationSubscribers.add(goalEstimationEventSubscriber);

            System.err.println("Offering goal: " + goal.getLabel());
            EventBusService.post(new GoalOfferEvent(goal));
        });

        boolean estimationsCompleted = false;

        // TODO: This way of waiting is pretty ugly. Lets find a better way.

        // wait for agents to estimate goals
        while (!estimationsCompleted) {
            boolean allSubscribersCompleted = true;
            for (GoalEstimationEventSubscriber subscriber : goalEstimationSubscribers) {
                boolean allAgentsCompleted = true;
                for (int agentEstimation : subscriber.getAgentStepsEstimation().values()) {
                    if (agentEstimation == -1) {
                        allAgentsCompleted = false;
                    }
                }
                if (!allAgentsCompleted) {
                    allSubscribersCompleted = allAgentsCompleted;
                }
            }
            estimationsCompleted = allSubscribersCompleted;
        }

        // Get the goal estimations
        for (GoalEstimationEventSubscriber subscriber : goalEstimationSubscribers) {

            String lowestEstimationAgent = null;
            int lowestEstimation = Integer.MAX_VALUE;
            Hashtable<String, Integer> agentStepsEstimation = subscriber.getAgentStepsEstimation();

            // Find the lowest value
            for (Map.Entry<String, Integer> agentEstimationKeyEntry : agentStepsEstimation.entrySet()) {

                System.err.println("Recieved estimation "
                        + Integer.toString(agentEstimationKeyEntry.getValue())
                        + " for goal: " + subscriber.getGoal().getLabel());

                if (agentEstimationKeyEntry.getValue() < lowestEstimation) {
                    lowestEstimationAgent = agentEstimationKeyEntry.getKey();
                    lowestEstimation = agentEstimationKeyEntry.getValue();
                }
            }

            System.err.println("Assigning goal " + subscriber.getGoal().getLabel() + " to " + lowestEstimationAgent);
            EventBusService.post(new GoalAssignmentEvent(lowestEstimationAgent, subscriber.getGoal()));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {

        // We need a strategy for doing this in the multi-agent case.
        // currentPlans.put(event.getAgent().getLabel(), event.getPlan());

        System.err.println("Recieved offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getPlan().getActions()));
    }

    @Subscribe
    public void problemSolverdEventSubscriber(ProblemSolvedEvent event) {
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