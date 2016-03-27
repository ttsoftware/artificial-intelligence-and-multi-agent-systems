package dtu.agency.events.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agent.GoalEstimationEvent;

import java.util.List;
import java.util.PriorityQueue;

public class GoalEstimationEventSubscriber implements EventSubscriber<GoalEstimationEvent> {

    private final Goal goal;
    private PriorityQueue<GoalEstimationEvent> agentEstimations = new PriorityQueue<>(new GoalEstimationEventComparator());

    public GoalEstimationEventSubscriber(Goal goal, List<String> agentLabels) {
        this.goal = goal;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void changeSubscriber(GoalEstimationEvent event) {
        agentEstimations.add(event);
    }

    public PriorityQueue<GoalEstimationEvent> getAgentStepsEstimation() {
        return agentEstimations;
    }

    public Goal getGoal() {
        return goal;
    }
}
