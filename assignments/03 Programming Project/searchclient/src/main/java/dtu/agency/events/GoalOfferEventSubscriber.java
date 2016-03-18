package dtu.agency.events;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.planners.HTNPlanner;

public class GoalOfferEventSubscriber {

    private int steps;

    @Subscribe
    public void change(GoalOfferEvent event) {

        Goal goal = event.getGoal();

        // HTN agency
        HTNPlanner htnPlanner = new HTNPlanner(goal);
    }

    private void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
