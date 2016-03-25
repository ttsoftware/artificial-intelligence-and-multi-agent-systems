package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class BoxAtPrecondition extends Precondition {

    private Box box;
    private Position boxPosition;

    public BoxAtPrecondition(Box box, Position boxPosition) {
        this.box = box;
        this.boxPosition = boxPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public Box getBox() {
        return box;
    }
}