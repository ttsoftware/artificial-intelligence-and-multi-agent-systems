package dtu.agency.events.agency;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.events.AsyncEvent;
import dtu.agency.planners.plans.ConcretePlan;

import java.util.LinkedList;

public class MoveObstacleAssignmentEvent extends AsyncEvent<ConcretePlan> {

    private final Agent agent;
    private final LinkedList<Position> path;
    private final BoardObject obstacle;

    public MoveObstacleAssignmentEvent(Agent agent, LinkedList<Position> path, BoardObject obstacle) {
        this.agent = agent;
        this.path = path;
        this.obstacle = obstacle;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public BoardObject getObstacle() {
        return obstacle;
    }

    public Agent getAgent() {
        return agent;
    }
}