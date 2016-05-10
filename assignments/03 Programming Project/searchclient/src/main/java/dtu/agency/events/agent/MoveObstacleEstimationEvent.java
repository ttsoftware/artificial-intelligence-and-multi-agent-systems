package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.EstimationEvent;

import java.util.LinkedList;

public class MoveObstacleEstimationEvent extends EstimationEvent {

    private final LinkedList<Position> path;
    private final boolean solvesObstacle;

    public MoveObstacleEstimationEvent(Agent agent,
                                       BoardObject obstacle,
                                       LinkedList<Position> path,
                                       boolean solvesObstacle) {
        super(obstacle, agent, path.size());
        this.solvesObstacle = solvesObstacle;
        this.path = path;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public boolean isSolvedObstacle() {
        return solvesObstacle;
    }
}