package dtu.agents;

import dtu.planners.Plan;
import dtu.planners.firstorder.actions.Action;

public class Agent implements Runnable {

    private Plan plan;

    public Agent(Plan plan) {
        this.plan = plan;
    }

    public void performAction(Action action) {
        switch (action.getType()) {
            case MOVE: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PUSH: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PULL: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            default: { // NONE
                // Do nothing?
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        }
    }

    @Override
    public void run() {

        // partial order planning

        getPlan().getActions().forEach(this::performAction);
    }

    public Plan getPlan() {
        return plan;
    }
}
