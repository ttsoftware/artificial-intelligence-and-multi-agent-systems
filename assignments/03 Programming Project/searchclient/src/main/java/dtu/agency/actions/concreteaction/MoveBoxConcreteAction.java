package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Box;

public abstract class MoveBoxConcreteAction extends ConcreteAction {

    protected final Box box;
    protected final Direction agentDirection;  // direction to box from agent POV
    protected final Direction boxDirection;    // desired direction the box should move

    public MoveBoxConcreteAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    public Box getBox() {
        return box;
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }
}
