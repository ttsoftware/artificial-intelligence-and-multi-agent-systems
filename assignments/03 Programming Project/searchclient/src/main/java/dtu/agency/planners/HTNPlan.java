package dtu.agency.planners;

import dtu.agency.planners.actions.HLAction;

import java.util.List;

public class HTNPlan implements AbstractPlan {

    private List<HLAction> actions;

    public HTNPlan(List<HLAction> actions) {
        this.actions = actions;
    }

    @Override
    public List<HLAction> getActions() {
        return this.actions;
    }
}