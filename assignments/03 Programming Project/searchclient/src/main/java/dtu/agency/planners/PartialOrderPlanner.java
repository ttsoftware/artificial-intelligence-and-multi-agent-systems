package dtu.agency.planners;

import dtu.agency.planners.actions.AbstractAction;

import java.util.ArrayList;

public class PartialOrderPlanner {

    public PartialOrderPlanner(AbstractAction action) {

    }

    public POPPlan plan() {
        return new POPPlan(new ArrayList<>());
    }
}
