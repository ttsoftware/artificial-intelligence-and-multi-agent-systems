package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;

public class NoConcreteAction extends ConcreteAction {

    public NoConcreteAction() {
    }

    public NoConcreteAction(NoConcreteAction no) {
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
