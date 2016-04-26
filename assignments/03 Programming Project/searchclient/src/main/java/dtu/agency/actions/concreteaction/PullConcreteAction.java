package dtu.agency.actions.concreteaction;

import dtu.agency.board.Box;

public class PullConcreteAction extends MoveBoxConcreteAction {

    public PullConcreteAction(Box box, Direction agentDirection, Direction directionToBox) {
        super(box, agentDirection, directionToBox);
    }

    public PullConcreteAction(PullConcreteAction pull) {
        super(pull);
    }

    @Override
    public Direction getBoxMovingDirection() {
        return this.boxDirection.getInverse();
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