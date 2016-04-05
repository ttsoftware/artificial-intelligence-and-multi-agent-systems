package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.planners.ConcretePlan;

import java.util.Stack;

public class POPPlan implements ConcretePlan {

    private final Stack<Action> actions;

    public POPPlan(Stack<Action> actions) {
        this.actions = actions;
    }

    @Override
    public Stack<Action> getActions() {
        return actions;
    }
}
