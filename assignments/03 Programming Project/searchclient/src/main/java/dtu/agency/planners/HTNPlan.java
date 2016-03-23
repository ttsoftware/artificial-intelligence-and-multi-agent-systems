package dtu.agency.planners;

import dtu.agency.planners.actions.HLAction;

import java.util.LinkedList;
import java.util.List;

//public class HTNPlan implements AbstractPlan {
public class HTNPlan {

    private LinkedList<HLAction> actions;

    public HTNPlan(List<HLAction> actions) {
        this.actions = new LinkedList<>(actions);
    }

    public List<HLAction> getActions() {
        return this.actions;
    }

    public void addAction(HLAction a) {
        this.actions.add(a);
    }

    public void clearActions() {
        this.actions.clear();
    }
}