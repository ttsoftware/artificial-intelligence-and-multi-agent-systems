package dtu.agency.events.agent;

import dtu.agency.events.Event;

public class GoalEstimationEvent extends Event implements Comparable<GoalEstimationEvent> {

    private String agentLabel;
    private int steps;

    public GoalEstimationEvent(String agentLabel, int steps) {
        this.agentLabel = agentLabel;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public String getAgentLabel() {
        return agentLabel;
    }

    @Override
    public int compareTo(GoalEstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}