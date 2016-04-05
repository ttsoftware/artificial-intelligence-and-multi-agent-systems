package dtu.agency.planners;

import dtu.agency.actions.ConcreteAction;

import java.util.LinkedList;
import java.util.Stack;

public class PrimitivePlan implements ConcretePlan {

    private Stack<ConcreteAction> concreteActions;

    public PrimitivePlan(LinkedList<ConcreteAction> plan) {
        concreteActions.addAll(plan);
    }

    public Stack<ConcreteAction> getActions() {
        return concreteActions;
    }

    public boolean isEmpty() { return this.concreteActions.isEmpty(); }

    @Override
    public String toString() {
        return "PrimitivePlan:"+ getActions().toString();
    }
}
