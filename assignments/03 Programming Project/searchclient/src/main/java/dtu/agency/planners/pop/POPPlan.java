package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.ConcretePlan;

import java.util.Stack;

public class POPPlan implements ConcretePlan {

    private final Stack<ConcreteAction> concreteActions;

    public POPPlan(Stack<ConcreteAction> concreteActions) {
        this.concreteActions = concreteActions;
    }

    @Override
    public Stack<ConcreteAction> getActions() {
        return concreteActions;
    }
}
