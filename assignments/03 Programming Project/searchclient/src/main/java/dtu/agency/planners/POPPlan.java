package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.List;

public class POPPlan implements ConcretePlan {

    private final List<Action> actions;

    public POPPlan(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
