package dtu.agency.events.agency;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.Event;

import java.util.LinkedList;

/**
 * An agent posts this event, when it encounters an obstacle he cannot move by himself
 */
public class MoveObstacleOfferEvent extends Event {

    private final Agent agent;
    private final LinkedList<Position> path;
    private final BoardObject obstacle;

    public MoveObstacleOfferEvent(Agent agent, LinkedList<Position> path, BoardObject obstacle) {
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
