package dtu.agency.planners;

import dtu.agency.planners.actions.AbstractAction;

import java.util.List;

public class HTNPlan implements AbstractPlan {

    private List<AbstractAction> actions;

    public HTNPlan(List<AbstractAction> actions) {
        this.actions = actions;
    }

    @Override
    public List<AbstractAction> getActions() {
        return this.actions;
    }
}