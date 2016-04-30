package dtu.agency.events.agent;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.AsyncEvent;

import java.util.LinkedList;

/**
 * An agent posts this event, when it encounters an obstacle he cannot move by himself
 */
public class HelpMoveObstacleEvent extends AsyncEvent<Boolean> {

    private final LinkedList<Position> path;
    private final BoardObject obstacle;

    public HelpMoveObstacleEvent(LinkedList<Position> path, BoardObject obstacle) {
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
