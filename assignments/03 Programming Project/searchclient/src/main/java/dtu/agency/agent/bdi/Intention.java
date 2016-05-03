package dtu.agency.agent.bdi;

import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.LinkedList;

public class Intention {

    protected final PrimitivePlan pseudoPlan;
    // ordered position of obstacles encountered
    protected final LinkedList<Position> obstaclePositions;

    // obstacles placed so that the agent may move them without moving the target box first (including the target box, ordered by distance from agent)
    protected final int reachableObstacles;
    // obstacles placed so that the agent cannot move them before moving the target box
    protected final int unreachableObstacles;

    public Intention(PrimitivePlan pseudoPlan,
                     LinkedList<Position> obstaclePositions,
                     int reachableObstacles,
                     int unreachableObstacles) {

        this.pseudoPlan = pseudoPlan;
        this.obstaclePositions = obstaclePositions;
        this.reachableObstacles = reachableObstacles;
        this.unreachableObstacles = unreachableObstacles;
    }

    public PrimitivePlan getPseudoPlan() {
        return pseudoPlan;
    }

    public int getObstacleCount() {
        return reachableObstacles + unreachableObstacles;
    }

    public LinkedList<Position> getObstaclePositions() {
        return obstaclePositions;
    }

    public int getReachableObstacles() {
        return reachableObstacles;
    }

    public int getUnreachableObstacles() {
        return unreachableObstacles;
    }
}
