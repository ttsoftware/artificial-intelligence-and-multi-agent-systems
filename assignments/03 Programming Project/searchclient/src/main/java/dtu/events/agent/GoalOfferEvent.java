package dtu.events.agent;

import dtu.board.Goal;
import dtu.events.Event;

public class GoalOfferEvent extends Event {

    private Goal goal;

    public GoalOfferEvent(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}