package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.Event;

import java.util.LinkedList;

public class MoveObstacleEstimationEvent extends Event implements Comparable<MoveObstacleEstimationEvent> {

    private final Agent agent;
    private final BoardObject obstacle;
    private final int steps;
    private final LinkedList<Position> path;
    private final boolean solvesObstacle;

    public MoveObstacleEstimationEvent(Agent agent, BoardObject obstacle, LinkedList<Position> path, boolean solvesObstacle) {
        this.agent = agent;
        this.obstacle = obstacle;
        this.solvesObstacle = solvesObstacle;
        this.steps = path.size();
        this.path = path;
    }

    public int getSteps() {
        return steps;
    }

    public Agent getAgent() {
        return agent;
    }

    public BoardObject getObstacle() {
        return obstacle;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public boolean isSolvesObstacle() {
        return solvesObstacle;
    }

    @Override
    public int compareTo(MoveObstacleEstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}