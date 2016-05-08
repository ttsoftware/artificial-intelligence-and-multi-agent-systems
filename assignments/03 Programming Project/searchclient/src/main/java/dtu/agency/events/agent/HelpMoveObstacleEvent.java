package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.AsyncEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * An agent posts this event, when it encounters an obstacle he cannot move by himself
 */
public class HelpMoveObstacleEvent extends AsyncEvent<List<LinkedList<Position>>> {

    private final Agent agent;
    private final LinkedList<Position> path;
    private final BoardObject obstacle;

    public HelpMoveObstacleEvent(Agent agent, LinkedList<Position> path, BoardObject obstacle) {
        this.agent = agent;
        this.path = path;
        this.obstacle = obstacle;
    }

    public Agent getAgent() {
        return agent;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public BoardObject getObstacle() {
        return obstacle;
    }
}
