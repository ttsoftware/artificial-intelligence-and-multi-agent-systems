package dtu.events;

import dtu.board.Goal;

import java.io.Serializable;

public class GoalOfferEvent implements Serializable {

    private Goal goal;

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}