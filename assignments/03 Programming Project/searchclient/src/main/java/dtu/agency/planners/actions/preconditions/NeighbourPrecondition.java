package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;

import java.util.List;

public class NeighbourPrecondition extends Precondition {

    private BoardObject object;
    private Position position;

    public NeighbourPrecondition(BoardObject object, Position position) {
        this.object = object;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public BoardObject getObject() {
        return object;
    }
}
