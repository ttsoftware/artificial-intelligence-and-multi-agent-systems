package dtu.agency.agent.actions;

import dtu.agency.planners.actions.effects.HTNEffect;

public class NoAction extends Action {

    public NoAction() {
        // I do nothing at all
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

    @Override
    public String toString() {
        return "NoOp";
    }

    @Override
    public HTNEffect applyTo(HTNEffect oldState) {
        return oldState;
    }
}
