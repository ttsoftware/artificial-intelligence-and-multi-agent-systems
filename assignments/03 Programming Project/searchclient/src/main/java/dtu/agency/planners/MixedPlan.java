package dtu.agency.planners;


import dtu.agency.AbstractAction;

import java.util.LinkedList;
import java.util.List;

public class MixedPlan implements AbstractPlan {

    private LinkedList<AbstractAction> actions;

    public MixedPlan(List<AbstractAction> actions) {
        this.actions = new LinkedList<>(actions);
    }

    @Override
    public LinkedList<AbstractAction> getActions() {
        return actions;
    }

    public void addAction(AbstractAction a) {
        this.actions.add(a);
    }

    public void clearActions() {
        this.actions.clear();
    }
}
