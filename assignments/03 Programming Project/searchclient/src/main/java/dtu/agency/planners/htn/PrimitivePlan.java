package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.ConcretePlan;

import java.util.LinkedList;

public class PrimitivePlan implements ConcretePlan {

    private LinkedList<ConcreteAction> concreteActions;

    public PrimitivePlan() {
        concreteActions = new LinkedList<>();
    }

    public LinkedList<ConcreteAction> getActions() {
        return concreteActions;
    }

    public LinkedList<ConcreteAction> getActionList() {
        return new LinkedList<>(concreteActions);
    }

    public ConcreteAction popAction() {
        return concreteActions.pollFirst();
    }

    public void pushAction( ConcreteAction action) {
        concreteActions.addFirst(action);
    }

    public void addAction( ConcreteAction action) {
        concreteActions.addLast(action);
    }

    public boolean isEmpty() { return this.concreteActions.isEmpty(); }

    public int size() { return this.concreteActions.size(); }

    @Override
    public String toString() {
        return "PrimitivePlan:"+ getActions().toString();
    }

    public void appendActions(PrimitivePlan other) {
        concreteActions.addAll(other.getActionList());
    }
}
