package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.LinkedList;
import java.util.List;

public class PrimitivePlan implements ConcretePlan {

    private LinkedList<Action> actions;

    public PrimitivePlan(List<Action> actions) {
        this.actions = new LinkedList<>(actions);
    }

    @Override
    public LinkedList<Action> getActions() {
        return actions;
    }
}
