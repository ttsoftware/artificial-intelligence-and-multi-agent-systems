package dtu.agency.events.agent;

import dtu.agency.events.Event;

public class GoalEstimationEvent extends Event {

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
}