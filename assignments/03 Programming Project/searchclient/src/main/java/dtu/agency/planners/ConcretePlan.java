package dtu.agency.planners;

import dtu.agency.actions.ConcreteAction;

import java.util.Stack;

public interface ConcretePlan extends Plan<ConcreteAction> {
    Stack<ConcreteAction> getActions();
}
