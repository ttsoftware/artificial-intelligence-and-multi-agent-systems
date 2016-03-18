package dtu.agency.events;

import dtu.agency.board.Goal;

import java.io.Serializable;

public class GoalOfferEvent implements Serializable {

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