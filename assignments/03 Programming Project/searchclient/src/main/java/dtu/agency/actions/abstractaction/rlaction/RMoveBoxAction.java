package dtu.agency.actions.abstractaction.rlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class RMoveBoxAction extends RLAction {

    private final Box box;
    private final Position boxDestination;

    public RMoveBoxAction(Box box, Position boxDestination) {
        this.box = box;
        this.boxDestination = boxDestination;
    }

    @Override
    public AbstractActionType getType() {
    return AbstractActionType.MoveBoxAction;
    }

    @Override
    public Position getDestination() {
        return boxDestination;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("RMoveBoxAction(");
        s.append(getBox().toString());
        s.append("->");
        s.append(boxDestination.toString());
        s.append(")");
        return s.toString();
    }
}
