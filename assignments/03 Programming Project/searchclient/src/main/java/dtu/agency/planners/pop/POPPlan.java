package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.ConcretePlan;

import java.util.LinkedList;

public class POPPlan implements ConcretePlan {

    private final LinkedList<ConcreteAction> concreteActions;

    public POPPlan(LinkedList<ConcreteAction> concreteActions) {
        this.concreteActions = concreteActions;
    }

    @Override
    public LinkedList<ConcreteAction> getActions() {
        return concreteActions;
    }

    @Override
    public ConcreteAction popAction() {
        return concreteActions.getFirst();
    }

    @Override
    public int approximateSteps(Position agentInitialPosition) {
        return Integer.MAX_VALUE;
    }
}
