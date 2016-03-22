package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.List;

public class PrimitivePlan implements ConcretePlan {

    private final List<Action> actions;

    public PrimitivePlan(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
