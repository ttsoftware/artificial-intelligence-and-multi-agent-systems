package dtu.agency.agent.actions;

import dtu.agency.planners.htn.HTNState;

public class NoAction extends Action {

    public NoAction() {
        // I do nothing at all
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

    @Override
    public HTNState applyTo(HTNState oldState) {
        return oldState;
    }

    @Override
    public String toString() {
        return "NoOp";
    }
}
