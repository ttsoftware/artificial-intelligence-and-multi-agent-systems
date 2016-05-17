package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.EstimationEvent;

public class GoalEstimationEvent extends EstimationEvent {

    private final Goal goal;

    public GoalEstimationEvent(Agent agent, Goal goal, int steps) {
        super(goal, agent, steps);
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }
}