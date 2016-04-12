package dtu.agency.actions.concreteaction;

import dtu.agency.board.Box;

public class PushConcreteAction extends MoveBoxConcreteAction {

    public PushConcreteAction(Box box, Direction agentDirection, Direction boxDirection) {
        super(box, agentDirection, boxDirection);
    }

    public PushConcreteAction(PushConcreteAction push) {
        super((MoveBoxConcreteAction) push);
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.PUSH;
    }

    @Override
    public String toString() {
        return "Push(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }
}