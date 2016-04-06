package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.htn.HTNState;

public class NoConcreteAction extends ConcreteAction {

    public NoConcreteAction() {
        // I do nothing at all
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.NONE;
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
