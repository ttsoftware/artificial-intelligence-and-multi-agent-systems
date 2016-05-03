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

    public GoalIntention(GoalIntention other) {
        super(
                other.getTargetBox(),
                new PrimitivePlan(other.pseudoPlan),
                other.getAgentPseudoPath(),
                other.getAgentBoxPseudoPath(),
                new LinkedList<>(other.obstaclePositions),
                other.getReachableObstacles(),
                other.getUnreachableObstacles()
        );
        this.goal = new Goal(other.goal);
    }

    public GoalIntention(Goal target,
                         Box box,
                         PrimitivePlan plan,
                         LinkedList<Position> agentPseudoPath,
                         LinkedList<Position> agentBoxPseudoPath,
                         LinkedList<Position> obstacles,
                         int reachable,
                         int unreachable) {
        super(
                box,
                plan,
                agentPseudoPath,
                agentBoxPseudoPath,
                obstacles,
                reachable,
                unreachable
        );
        goal = target;
    }

    @Override
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
}
