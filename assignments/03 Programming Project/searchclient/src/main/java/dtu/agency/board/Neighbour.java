package dtu.agency.board;

import dtu.agency.actions.concreteaction.Direction;

public class Neighbour {

    private final Position position;
    private final Direction direction;
    private final int depth;

    public Neighbour(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
        this.depth = 1;
    }

    public Neighbour(Position position, int depth) {
        this.position = position;
        this.direction = null;
        this.depth = depth;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDepth() {
        return depth;
    }
}
