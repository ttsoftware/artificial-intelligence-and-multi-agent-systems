package dtu.agency.planners;

import dtu.agency.actions.ConcreteAction;

import java.util.Collection;

public interface ConcretePlan extends Plan<ConcreteAction> {
    Collection<ConcreteAction> getActions();
    ConcreteAction popAction();
}
