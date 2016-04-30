package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.events.Event;

public class MoveObstacleEstimationEvent extends Event implements Comparable<MoveObstacleEstimationEvent> {

    private final Agent agent;
    private final BoardObject obstacle;
    private final int steps;

    public MoveObstacleEstimationEvent(Agent agent, BoardObject obstacle, int steps) {
        this.agent = agent;
        this.obstacle = obstacle;
        this.steps = steps;
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

    @Override
    public int compareTo(MoveObstacleEstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}