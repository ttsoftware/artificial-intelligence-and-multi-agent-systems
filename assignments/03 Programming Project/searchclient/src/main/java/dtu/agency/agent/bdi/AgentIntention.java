package dtu.agency.agent.bdi;


import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;

import java.util.LinkedList;

public class AgentIntention {
    // (top level) Intentions are really SolveGoalSuperActions()
    // but could also be other orders issued by TheAgency
    public final Goal goal; // Goal to be solved
    public final Box targetBox;  // Box that solves the goal
    public final PrimitivePlan pseudoPlan;
    public final LinkedList<Position> pseudoPath;           // agent's core path, ordered without duplicates
    public final LinkedList<Position> obstaclePositions;    // ordered position of obstacles encountered
    private final int reachableObstacles;   // obstacles placed so that the agent may move them without moving the target box first (including the target box, ordered by distance from agent)
    private final int unreachableObstacles; // obstacles placed so that the agent cannot move them before moving the target box

    public AgentIntention(AgentIntention other) {
        this.goal = new Goal(other.goal);
        this.targetBox = new Box(other.targetBox);
        this.pseudoPlan = new PrimitivePlan(other.pseudoPlan);
        this.pseudoPath = new LinkedList<>(other.pseudoPath);
        this.obstaclePositions = new LinkedList<>(other.obstaclePositions);
        this.reachableObstacles = other.reachableObstacles;
        this.unreachableObstacles = other.unreachableObstacles;
    }

    public AgentIntention(Goal target, Box box, PrimitivePlan plan, LinkedList<Position> path, LinkedList<Position> obstacles, int reachable, int unreachable) {
        goal = target;
        targetBox = box;
        pseudoPlan = plan;
        pseudoPath = path;
        obstaclePositions = obstacles;
        reachableObstacles = reachable;
        unreachableObstacles = unreachable;
    }

    public int getApproximateSteps() {
        // TODO : WEIGHTS SHOULD BE CONFIGURED CENTRAL LOCATION
        int weightPathLength = 1;  // Weight for length of path
        int weightReachable = 2;  // Weight for reachable boxes
        int weightUnreachable = 12; // Weight for unreachable boxes
        if (pseudoPath == null) {
            return Integer.MAX_VALUE;
        } else {
            return pseudoPath.size() * weightPathLength
                    + unreachableObstacles * weightUnreachable
                    + ((reachableObstacles > 0) ? unreachableObstacles - 1 : 0) * weightReachable; // -1 for eliminating the target box itself from punishment
        }
    }

    @Override
    public String toString() {
        return "AgentIntention: Agent " + BDIService.getInstance().getAgent()
                + " intends to solve goal " + goal + " using box " + targetBox + "\n"
                + "using a path of length " + pseudoPath.size() + " with "
                + reachableObstacles + "/" + unreachableObstacles
                + "reachable/unreachable obstacles in path";

    }

    public int getObstacleCount() {
        return reachableObstacles + unreachableObstacles;
    }

    public LinkedList<Position> getPseudoPath() {
        return new LinkedList<>(pseudoPath);
    }
}
