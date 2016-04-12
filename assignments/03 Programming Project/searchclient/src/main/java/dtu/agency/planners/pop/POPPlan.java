package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.ConcretePlan;

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
}
