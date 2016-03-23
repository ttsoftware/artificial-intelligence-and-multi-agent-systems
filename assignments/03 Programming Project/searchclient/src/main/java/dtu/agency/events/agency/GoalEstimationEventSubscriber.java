package dtu.agency.events.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agent.GoalEstimationEvent;

import java.util.HashMap;
import java.util.List;

public class GoalEstimationEventSubscriber implements EventSubscriber<GoalEstimationEvent> {

    private final Goal goal;
    // Agent label -> estimated steps to complete goal
    private final HashMap<String, Integer> agentStepsEstimation;

    public GoalEstimationEventSubscriber(Goal goal, List<String> agentLabels) {
        this.goal = goal;
        this.agentStepsEstimation = new HashMap<>();
        agentLabels.forEach(label -> {
            // initialize all estimations to -1
            agentStepsEstimation.put(label, -1);
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void changeSubscriber(GoalEstimationEvent event) {
        agentStepsEstimation.put(event.getLabel(), event.getSteps());
    }

    public HashMap<String, Integer> getAgentStepsEstimation() {
        return agentStepsEstimation;
    }

    public Goal getGoal() {
        return goal;
    }
}
