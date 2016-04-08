package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;

public class NoConcreteAction extends ConcreteAction {

    public NoConcreteAction() {
        // I do nothing at all
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.NONE;
    }

    @Override
    public String toString() {
        return "NoOp";
    }

}
