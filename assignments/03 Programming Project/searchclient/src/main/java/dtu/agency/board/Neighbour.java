package dtu.agency.board;

import dtu.agency.agent.actions.Direction;

public class Neighbour {

    private final Position position;
    private final Direction direction;

    public Neighbour(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }
}
