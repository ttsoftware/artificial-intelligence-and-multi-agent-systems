package dtu.agency.events.agent;

import dtu.agency.events.Event;

public class GoalEstimationEvent extends Event {

    private String label;
    private int steps;

    public GoalEstimationEvent(String label, int steps) {
        this.label = label;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public String getLabel() {
        return label;
    }
}