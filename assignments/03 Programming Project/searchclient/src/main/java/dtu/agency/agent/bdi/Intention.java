package dtu.agency.agent.bdi;

import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.LinkedList;

public abstract class Intention {

    // Box that we wish to move the goal
    protected final Box targetBox;

    protected final PrimitivePlan pseudoPlan;

    // agent's core path, ordered without duplicates
    protected final LinkedList<Position> agentPseudoPath;

    // agent's core path, ordered without duplicates
    protected final LinkedList<Position> agentBoxPseudoPath;

    // ordered position of obstacles encountered
    protected final LinkedList<Position> obstaclePositions;

    // obstacles placed so that the agent may move them without moving the target box first (including the target box, ordered by distance from agent)
    protected final int reachableObstacles;
    // obstacles placed so that the agent cannot move them before moving the target box
    protected final int unreachableObstacles;

    public Intention(Box targetBox,
                     PrimitivePlan pseudoPlan,
                     LinkedList<Position> agentPseudoPath,
                     LinkedList<Position> agentBoxPseudoPath,
                     LinkedList<Position> obstaclePositions,
                     int reachableObstacles,
                     int unreachableObstacles) {
        this.targetBox = targetBox;
        this.pseudoPlan = pseudoPlan;
        this.agentPseudoPath = agentPseudoPath;
        this.agentBoxPseudoPath = agentBoxPseudoPath;
        this.obstaclePositions = obstaclePositions;
        this.reachableObstacles = reachableObstacles;
        this.unreachableObstacles = unreachableObstacles;
    }

    public Box getTargetBox() {
        return targetBox;
    }

    public PrimitivePlan getPseudoPlan() {
        return pseudoPlan;
    }

    public int getObstacleCount() {
        return reachableObstacles + unreachableObstacles;
    }

    public LinkedList<Position> getAgentBoxPseudoPath() {
        return agentBoxPseudoPath;
    }

    public LinkedList<Position> getAgentBoxPseudoPathClone() {
        return new LinkedList<>(agentBoxPseudoPath);
    }

    public LinkedList<Position> getAgentPseudoPath() {
        return agentPseudoPath;
    }

    public LinkedList<Position> getAgentPseudoPathClone() {
        return new LinkedList<>(agentPseudoPath);
    }

    public LinkedList<Position> getObstaclePositions() {
        return obstaclePositions;
    }

    public LinkedList<Position> getObstaclePositionsClone() {
        return new LinkedList<>(obstaclePositions);
    }

    public int getReachableObstacles() {
        return reachableObstacles;
    }

    public int getUnreachableObstacles() {
        return unreachableObstacles;
    }

    public abstract int getApproximateSteps();
}
