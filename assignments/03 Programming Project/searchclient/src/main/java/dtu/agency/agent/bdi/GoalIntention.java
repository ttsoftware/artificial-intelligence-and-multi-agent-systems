package dtu.agency.agent.bdi;


import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class GoalIntention extends Intention {
    // (top level) Intentions are really SolveGoalSuperActions()
    // but could also be other orders issued by TheAgency
    private final Goal goal; // Goal to be solved

    public GoalIntention(GoalIntention other) {
        super(
                other.getTargetBox(),
                new PrimitivePlan(other.pseudoPlan),
                other.getAgentPseudoPathClone(),
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
                         LinkedList<Position> obstaclePositions,
                         int reachable,
                         int unreachable) {
        super(
                box,
                plan,
                agentPseudoPath,
                agentBoxPseudoPath,
                obstaclePositions,
                reachable,
                unreachable
        );
        goal = target;
    }

    @Override
    public int getApproximateSteps() {
        int approximation = getApproximateStepsWithWeights(
                1,
                6,
                12,
                24
        );
        return approximation;
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
