package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.ConcretePlan;

import java.util.Stack;

public class PrimitivePlan implements ConcretePlan {

    private Stack<ConcreteAction> concreteActions = new Stack<>();

    public PrimitivePlan() {
        concreteActions = new Stack<>();
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
