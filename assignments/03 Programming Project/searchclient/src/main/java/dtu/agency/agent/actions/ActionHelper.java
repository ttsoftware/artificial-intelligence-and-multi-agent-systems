package dtu.agency.agent.actions;

import dtu.agency.board.Position;

public class ActionHelper {

    public static Position getNextPositionFromMovingDirection(Position position, Direction direction) {
        switch (direction) {
            case NORTH:
                return new Position(position.getRow()-1, position.getColumn());
            case SOUTH:
                return new Position(position.getRow()+1, position.getColumn());
            case WEST:
                return new Position(position.getRow(), position.getColumn()-1);
            case EAST:
                return new Position(position.getRow(), position.getColumn()+1);
            default:
                return position;
        }
    }
}
