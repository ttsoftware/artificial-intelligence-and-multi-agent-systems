package dtu.agency.planners;

import dtu.agency.planners.actions.AbstractAction;

import java.util.LinkedList;
import java.util.List;

public class MixedPlan implements AbstractPlan {

    private LinkedList<AbstractAction> actions;

    public MixedPlan() {
        this.actions = new LinkedList<>();
    }

    public MixedPlan(List<? extends AbstractAction> actions) {
        this.actions = new LinkedList<>(actions);
    }


    public void addAction(AbstractAction a) {
        this.actions.add(a);
    }

    @Override
    public LinkedList<AbstractAction> getActions() {
        return actions;
    }

    public AbstractAction getFirst() { return actions.peekFirst(); }
    public AbstractAction removeFirst() { return actions.removeFirst(); }
    public void extend(MixedPlan plan) {
        actions.addAll(plan.getActions());
    }
    public boolean isEmpty() { return actions.isEmpty(); }

    @Override
    public String toString() {
        return "MixedPlan:"+getActions().toString();
    }

}
