package dtu.agency.agent.bdi;


import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;

import java.util.LinkedList;

public class GoalIntention extends Intention {
    // (top level) Intentions are really SolveGoalSuperActions()
    // but could also be other orders issued by TheAgency
    private final Goal goal; // Goal to be solved
    private final Box targetBox;  // Box that solves the goal
    private final LinkedList<Position> agentPseudoPath;           // agent's core path, ordered without duplicates
    private final LinkedList<Position> agentBoxPseudoPath;           // agent's core path, ordered without duplicates

    public GoalIntention(GoalIntention other) {
        super(
                new PrimitivePlan(other.pseudoPlan),
                new LinkedList<>(other.obstaclePositions),
                other.getReachableObstacles(),
                other.getUnreachableObstacles()
        );
        this.goal = new Goal(other.goal);
        this.targetBox = new Box(other.targetBox);
        this.agentPseudoPath = new LinkedList<>(other.agentPseudoPath);
        this.agentBoxPseudoPath = new LinkedList<>(other.agentPseudoPath);
    }

    public GoalIntention(Goal target,
                         Box box,
                         PrimitivePlan plan,
                         LinkedList<Position> agentPath,
                         LinkedList<Position> agentBoxPath,
                         LinkedList<Position> obstacles,
                         int reachable,
                         int unreachable) {
        super(
                plan,
                obstacles,
                reachable,
                unreachable
        );
        goal = target;
        targetBox = box;
        agentPseudoPath = agentPath;
        agentBoxPseudoPath = agentBoxPath;
    }

    public int getApproximateSteps() {
        // TODO : WEIGHTS SHOULD BE CONFIGURED CENTRAL LOCATION
        int weightPathLength = 1;  // Weight for length of path
        int weightReachable = 2;  // Weight for reachable boxes
        int weightUnreachable = 12; // Weight for unreachable boxes
        if (agentPseudoPath == null) {
            return Integer.MAX_VALUE;
        } else {
            return agentPseudoPath.size() * weightPathLength
                    + unreachableObstacles * weightUnreachable
                    + ((reachableObstacles > 0) ? unreachableObstacles - 1 : 0) * weightReachable; // -1 for eliminating the target box itself from punishment
        }
    }

    @Override
    public String toString() {
        return "GoalIntention: Agent " + BDIService.getInstance().getAgent()
                + " intends to solve goal " + goal + " using box " + targetBox + "\n"
                + "using a path of length " + agentPseudoPath.size() + " with "
                + reachableObstacles + "/" + unreachableObstacles
                + "reachable/unreachable obstacles in path";

    }

    public Goal getGoal() {
        return goal;
    }

    public Box getTargetBox() {
        return targetBox;
    }

    public LinkedList<Position> getAgentBoxPseudoPath() {
        return agentBoxPseudoPath;
    }

    public LinkedList<Position> getAgentBoxPseudoPathClone() {
        return new LinkedList<>(agentBoxPseudoPath);
    }

    public LinkedList<Position> getAgentPseudoPath() {
        return new LinkedList<>(agentPseudoPath);
    }
}
