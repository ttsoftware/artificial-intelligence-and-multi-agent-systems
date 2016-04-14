package dtu.agency.planners.plans;

import dtu.agency.actions.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * MixedPlan Makes for Entries of both concrete and abstract actions
 */
public class MixedPlan implements Plan {

    private final LinkedList<Action> actions;

    public MixedPlan() {
        this.actions = new LinkedList<>();
    }

    public MixedPlan(List<? extends Action> actions) {
        this.actions = new LinkedList<>(actions);
    }

    public MixedPlan(MixedPlan other) {
        this.actions = new LinkedList<>(other.getActions());
    }

    public void addAction(Action a) {
        this.actions.add(a);
    }

    @Override
    public LinkedList<Action> getActions() {
        return new LinkedList<>(actions);
    }

    public Action getFirst() { return actions.peekFirst(); }
    public Action removeFirst() { return actions.removeFirst(); }
    public void extend(MixedPlan plan) {
        actions.addAll(plan.getActions());
    }
    public boolean isEmpty() { return actions.isEmpty(); }

    @Override
    public String toString() {
        return "MixedPlan:"+ getActions().toString();
    }

}
