package dtu.agency.events.agency;

import dtu.agency.board.Goal;
import dtu.agency.events.Event;

public class GoalAssignmentEvent extends Event {

    private final String agentLabel;
    private final Goal goal;

    public GoalAssignmentEvent(String label, Goal goal) {
        this.agentLabel = label;
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public String getAgentLabel() {
        return agentLabel;
    }
}