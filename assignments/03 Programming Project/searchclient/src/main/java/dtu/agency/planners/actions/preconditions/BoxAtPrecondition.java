package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class BoxAtPrecondition extends Precondition {

    private Box box;
    private Position position;

    public BoxAtPrecondition(Box box, Position position) {
        this.box = box;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Box getBox() {
        return box;
    }
}