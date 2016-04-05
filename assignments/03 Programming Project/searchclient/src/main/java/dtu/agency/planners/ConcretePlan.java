package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.Stack;

public interface ConcretePlan extends Plan<Action> {
    Stack<Action> getActions();
}
