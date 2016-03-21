package dtu.agency.events.agency;

import dtu.agency.board.Goal;
import dtu.agency.events.Event;

public class GoalOfferEvent extends Event {

    private final Goal goal;

    public GoalOfferEvent(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }
}