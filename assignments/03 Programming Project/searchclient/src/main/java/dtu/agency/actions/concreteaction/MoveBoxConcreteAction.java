package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Box;

public abstract class MoveBoxConcreteAction extends ConcreteAction {

    protected final Box box;
    final Direction agentDirection;  // direction to box from agent POV
    final Direction boxDirection;    // desired direction the box should move

    MoveBoxConcreteAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    public MoveBoxConcreteAction(MoveBoxConcreteAction other) {
        this.box = new Box(other.getBox());
        this.agentDirection = other.getAgentDirection();
        this.boxDirection = other.getBoxDirection();
    }

    public Box getBox() {
        return box;
    }

    @Override
    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }
}
