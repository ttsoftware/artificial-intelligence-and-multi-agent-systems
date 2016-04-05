package dtu.agency.planners.pop.preconditions;

import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;

public class NeighbourPrecondition extends Precondition {

    private BoardObject object;
    private Position position;
    private Direction direction;

    public NeighbourPrecondition(BoardObject object, Position position, Direction direction) {
        this.object = object;
        this.position = position;
        this.direction = direction;
    }

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

    public Direction getDirection() {
        return direction;
    }
}
