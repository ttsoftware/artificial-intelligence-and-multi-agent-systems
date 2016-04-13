package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.ConcretePlan;

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

    @Override
    public ConcreteAction popAction() {
        return concreteActions.pop();
    }

    @Override
    public int approximateSteps(Position agentInitialPosition) {
        return Integer.MAX_VALUE;
    }
}
