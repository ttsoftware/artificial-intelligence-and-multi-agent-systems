package dtu.agency.actions.concreteaction;

import dtu.agency.board.Box;

public class PullConcreteAction extends MoveBoxConcreteAction {

    public PullConcreteAction(Box box, Direction agentDirection, Direction boxDirection) {
        super(box, agentDirection, boxDirection);
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + agentDirection + "," + boxDirection + ")";
    }
}