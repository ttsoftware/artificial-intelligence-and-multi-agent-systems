package dtu.planners;

import dtu.planners.actions.AbstractAction;

public class PartialOrderPlanner {

    private AbstractAction action;

    public PartialOrderPlanner(AbstractAction action) {
        this.action = action;
    }
}
