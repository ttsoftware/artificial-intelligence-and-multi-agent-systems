package dtu.agency.agent.bdi;


import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.util.LinkedList;

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
                box,
                plan,
                agentPseudoPath,
                agentBoxPseudoPath,
                obstacles,
                reachable,
                unreachable
        );
        this.originPath = originPath;
        this.combinedPath = mergePaths(agentBoxPseudoPath, originPath);
    }

    private LinkedList<Position> mergePaths(LinkedList<Position> newPath,
                                            LinkedList<Position> originPath) {

        LinkedList<Position> newPathReversed = reversePath(newPath);

        PlanningLevelService pls = new PlanningLevelService(
                BDIService.getInstance().getBDILevelService().getLevelClone()
        );

        // Move agent to the last position in its path
        pls.moveAgent(originPath.peekLast());

        // Plan for moving agent from its last position, to newPathReversed's first position
        RGotoAction extendPathAction = new RGotoAction(newPathReversed.peekFirst());

        HTNPlanner htn = new HTNPlanner(pls, extendPathAction, RelaxationMode.NoAgentsNoBoxes);
        PrimitivePlan pseudoPlan = htn.plan();

        // path going from originPath's last position, to newPathReversed's first position
        LinkedList<Position> connectingPath = pls.getOrderedPath(pseudoPlan);
        if (connectingPath.size() > 0) {
            connectingPath.removeFirst();
        }
        if (connectingPath.size() > 0) {
            connectingPath.removeLast();
        }

        // combine the two paths into originPath
        originPath.addAll(connectingPath);
        originPath.addAll(newPathReversed);

        return originPath;
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
        // TODO: Punish paths with many obstacles
        return originPath.size();
    }

    /**
     * Reverse all actions in given path
     * @param path
     * @return
     */
    private LinkedList<Position> reversePath(LinkedList<Position> path) {
        LinkedList<Position> newPath = new LinkedList<>();

        for (Position position : path) {
            newPath.addFirst(position);
        }

        return newPath;
    }
}
