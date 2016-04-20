package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.Event;

public class GoalEstimationEvent extends Event implements Comparable<GoalEstimationEvent> {

    private final Agent agent;
    private final Goal goal;
    private final int steps;

    public GoalEstimationEvent(Agent agent, Goal goal, int steps) {
        this.agent = agent;
        this.goal = goal;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public Agent getAgent() {
        return agent;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public int compareTo(GoalEstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}