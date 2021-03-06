package dtu.agency.agent.bdi;


import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class MoveBoxFromPathIntention extends Intention {

    private final LinkedList<Position> originPath;
    private final LinkedList<Position> combinedPath;

    public MoveBoxFromPathIntention(MoveBoxFromPathIntention other) {
        super(
                other.getTargetBox(),
                new PrimitivePlan(other.pseudoPlan),
                other.getAgentPseudoPathClone(),
                other.getAgentBoxPseudoPath(),
                new LinkedList<>(other.obstaclePositions),
                other.getReachableObstacles(),
                other.getUnreachableObstacles()
        );
        originPath = other.getOriginalPath();
        combinedPath = other.getCombinedPath();
    }

    public MoveBoxFromPathIntention(Box box,
                                    PrimitivePlan plan,
                                    LinkedList<Position> agentPseudoPath,
                                    LinkedList<Position> agentBoxPseudoPath,
                                    LinkedList<Position> obstacles,
                                    int reachable,
                                    int unreachable,
                                    LinkedList<Position> originPath) {
        super(
                new Box(box),
                new PrimitivePlan(plan),
                new LinkedList<>(agentPseudoPath),
                new LinkedList<>(agentBoxPseudoPath),
                new LinkedList<>(obstacles),
                reachable,
                unreachable
        );
        this.originPath = new LinkedList<>(originPath);
        this.combinedPath = BDIService.getInstance()
                .getBDILevelService()
                .mergePaths(this.agentBoxPseudoPath, this.originPath);
    }

    public LinkedList<Position> getOriginalPath() {
        return originPath;
    }

    /**
     * TODO: This path might fuck up the neighbour finding
     * Combined originPath = original originPath + agentBoxPseudoPath
     * @return
     */
    public LinkedList<Position> getCombinedPath() {
        return combinedPath;
    }

    @Override
    public int getApproximateSteps() {
        return getApproximateStepsWithWeights(
                1,
                12,
                0,
                24
        );
    }
}
