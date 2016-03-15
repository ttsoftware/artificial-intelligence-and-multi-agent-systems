package dtu.events;

import com.google.common.eventbus.Subscribe;
import dtu.board.Goal;
import dtu.planners.HTNPlanner;

public class GoalOfferEventSubscriber {

    private int steps;

    @Subscribe
    public void change(GoalOfferEvent event) {

        Goal goal = event.getGoal();

        // HTN planning
        HTNPlanner htnPlanner = new HTNPlanner(goal);
    }

    private void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}
