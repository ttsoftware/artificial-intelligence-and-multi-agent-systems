package dtu.agency.planners;

import dtu.agency.actions.ConcreteAction;

import java.util.LinkedList;
import java.util.Stack;

public class PrimitivePlan implements ConcretePlan {

    private Stack<ConcreteAction> concreteActions;

    public PrimitivePlan() {
        concreteActions = new Stack<>();
    }

    public PrimitivePlan(LinkedList<ConcreteAction> plan) {
        concreteActions = new Stack<>();
        concreteActions.addAll(plan); // Watch It!!! reverse order plan!
    }

    public Stack<ConcreteAction> getActions() {
        return concreteActions;
    }

    public void pushAction( ConcreteAction action) {
        concreteActions.push(action);
    }

    public boolean isEmpty() { return this.concreteActions.isEmpty(); }

    @Override
    public String toString() {
        return "PrimitivePlan:"+ getActions().toString();
    }
}
