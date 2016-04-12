package dtu.agency.planners;

import dtu.agency.actions.ConcreteAction;

import java.util.LinkedList;

public interface ConcretePlan extends Plan<ConcreteAction> {
    LinkedList<ConcreteAction> getActions();
    ConcreteAction popAction();
}
