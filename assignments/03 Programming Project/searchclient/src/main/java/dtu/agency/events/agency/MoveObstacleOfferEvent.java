package dtu.agency.events.agency;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.Event;

import java.util.LinkedList;

/**
 * An agent posts this event, when it encounters an obstacle he cannot move by himself
 */
public class MoveObstacleOfferEvent extends Event {

    private final LinkedList<Position> path;
    private final BoardObject obstacle;

    public MoveObstacleOfferEvent(LinkedList<Position> path, BoardObject obstacle) {
        this.path = path;
        this.obstacle = obstacle;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public BoardObject getObstacle() {
        return obstacle;
    }
}
