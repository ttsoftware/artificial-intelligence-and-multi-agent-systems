package dtu.agency.actions;

import dtu.agency.actions.concreteaction.*;

public abstract class ConcreteAction implements Action<ConcreteActionType> {

    protected int heuristicValue;

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public abstract Direction getAgentDirection();

    public static ConcreteAction cloneConcreteAction(ConcreteAction concreteAction) {
        switch (concreteAction.getType()) {
            case MOVE:
                MoveConcreteAction move = (MoveConcreteAction) concreteAction;
                return new MoveConcreteAction(move);

            case PUSH:
                PushConcreteAction push = (PushConcreteAction) concreteAction;
                return new PushConcreteAction(push);

            case PULL:
                PullConcreteAction pull = (PullConcreteAction) concreteAction;
                return new PullConcreteAction(pull);

            case NONE:
                NoConcreteAction no = (NoConcreteAction) concreteAction;
                return new NoConcreteAction(no);

            default:
                return null;
        }
    }
}