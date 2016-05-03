package dtu.agency.agent.bdi;


import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.LinkedList;

public class MoveBoxFromPathIntention extends Intention {

    private final LinkedList<Position> path;

    public MoveBoxFromPathIntention(MoveBoxFromPathIntention other) {
        super(
                other.getTargetBox(),
                new PrimitivePlan(other.pseudoPlan),
                other.getAgentPseudoPath(),
                other.getAgentBoxPseudoPath(),
                new LinkedList<>(other.obstaclePositions),
                other.getReachableObstacles(),
                other.getUnreachableObstacles()
        );
        path = other.getPath();
    }

    public MoveBoxFromPathIntention(Box box,
                                    PrimitivePlan plan,
                                    LinkedList<Position> agentPseudoPath,
                                    LinkedList<Position> agentBoxPseudoPath,
                                    LinkedList<Position> obstacles,
                                    int reachable,
                                    int unreachable,
                                    LinkedList<Position> path) {
        super(
                box,
                plan,
                agentPseudoPath,
                agentBoxPseudoPath,
                obstacles,
                reachable,
                unreachable
        );
        this.path = path;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    @Override
    public int getApproximateSteps() {
        // TODO: Punish bad paths
        return path.size();
    }
}
