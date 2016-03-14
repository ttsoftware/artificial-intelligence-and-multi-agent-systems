package dtu.agents;

import dtu.planners.firstorder.actions.Action;

import java.util.List;

public class Agent implements Runnable {

    private List<Action> actions;

    public Agent(List<Action> actions) {
        this.actions = actions;
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
        }
    }

    @Override
    public void run() {
        actions.forEach(this::performAction);
    }
}
