package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.events.Event;

public class GoalEstimationEvent extends Event implements Comparable<GoalEstimationEvent> {

    private Agent agent;
    private int steps;

    public GoalEstimationEvent(Agent agent, int steps) {
        this.agent = agent;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public Agent getAgent() {
        return agent;
    }

    @Override
    public int compareTo(GoalEstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}