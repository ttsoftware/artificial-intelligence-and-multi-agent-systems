package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class HLPlanner {
    private final PlanningLevelService pls;
    private final AgentIntention intention;
    private final HLPlan plan;
    private int committedActions;

    /**
     *
     * @param intention
     * @param pls
     */
    public HLPlanner(AgentIntention intention, PlanningLevelService pls) {
        this.pls  = pls;
        this.intention = intention;
        this.plan = new HLPlan();
        this.committedActions = 0;
    }

    public HLPlan plan() {

        ListIterator obstacles = intention.obstaclePositions.listIterator();
        int remainingObstacles = intention.obstaclePositions.size();
        LinkedList<Position> removedObstacles = new LinkedList<>();

        while (obstacles.hasNext()) {
            Position obstacleOrigin = (Position) obstacles.next();
            Box box = new Box(pls.getObjectLabels(obstacleOrigin));

            if (box.equals(intention.targetBox) && obstacles.hasNext()) {
                // move goal box to free position and try re-planning from there (recurse once)
                Position neighbour = pls.getValidNeighbour(intention.getPseudoPath(), obstacleOrigin, remainingObstacles);
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
                intention.obstaclePositions.removeAll(removedObstacles);

                System.err.println("RECURSION");
                return plan(); // Recursive behavior max depth is 1 :-)
            }
            else if (box.equals(intention.targetBox)) {
                // only 'obstacle' left in path is goal box - move it into goal position
                moveBoxInPlanner(box, pls.getPosition(intention.goal), intention.getPseudoPath().peekLast());
                pls.revertLast(committedActions);
                removedObstacles.add(obstacleOrigin);
                return plan;
            }
            else {
                // next obstacle is in the path - move box to free position
                Position neighbour = pls.getValidNeighbour(intention.getPseudoPath(), obstacleOrigin, remainingObstacles);
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
            }
            remainingObstacles--;
        }

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position

        moveBoxInPlanner(intention.targetBox, pls.getPosition(intention.goal), intention.getPseudoPath().peekLast()); // add hlaction to plan, committedactions+1, update pls
        pls.revertLast(committedActions);
        return plan;
    }

    private void moveBoxInPlanner(Box box, Position boxDestination, Position agentDestination) {
        Agent agent = BDIService.getInstance().getAgent();
        HMoveBoxAction moveBoxAction = new HMoveBoxAction(
                box,
                boxDestination,
                agentDestination
        );
        plan.append(moveBoxAction);
        pls.apply(moveBoxAction);
        committedActions++;
    }
}
