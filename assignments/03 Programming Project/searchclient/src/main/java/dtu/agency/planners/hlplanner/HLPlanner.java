package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;

import java.util.*;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class HLPlanner {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

    private PlanningLevelService pls;
    private AgentIntention intention;
    private HLPlan plan;
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
        debug("hl planning", 2);
        debug("hl planning! ", 20);
        // calculate the agent final destination
        // TODO: this is only in case of no unreachable obstacles!
        Position agentDestination = intention.getPseudoPath().peekLast();

        debug("obstacle count: " + intention.getObstacleCount());
        debug("", -20);

        ListIterator obstacles = intention.obstaclePositions.listIterator();
        int remainingObstacles = intention.obstaclePositions.size();
        LinkedList<Position> removedObstacles = new LinkedList<>();

        while (obstacles.hasNext()) {
            Position obstacleOrigin = (Position) obstacles.next();
            Box box = new Box(pls.getObjectLabels(obstacleOrigin));
            debug("obstacle: " + box + "@" + obstacleOrigin + " - goal box is " + intention.targetBox, 20);
            if (box.equals(intention.targetBox) && obstacles.hasNext()) {
                // TODO move goalbox to free position and try again (recurse)
                Position neighbour = pls.getValidNeighbour(intention.getPseudoPath(), obstacleOrigin, remainingObstacles);
                moveBoxInPlanner(box, neighbour, obstacleOrigin); // add hlaction to plan, committedactions+1, update pls
                removedObstacles.add(obstacleOrigin);
                intention.obstaclePositions.removeAll(removedObstacles);
                debug("obstacles left: " + intention.obstaclePositions);
                System.err.println("RECURSION");
                return plan(); // Recursive behavior max depth is 1 :-)
            } else if (box.equals(intention.targetBox)) {
                // TODO: move goal box into goal
                moveBoxInPlanner(box, pls.getPosition(intention.goal), agentDestination); // add hlaction to plan, committedactions+1, update pls
                // TODO: update BDI hlplan ?? NO this happens in the Mind.
                pls.revertLast(committedActions);
                removedObstacles.add(obstacleOrigin);
                return plan;
            } else {
                // TODO: move obstacle box to free position
                Position neighbour = pls.getValidNeighbour(intention.getPseudoPath(), obstacleOrigin, remainingObstacles);
                moveBoxInPlanner(box, neighbour, obstacleOrigin); // add hlaction to plan, committedactions+1, update pls
                removedObstacles.add(obstacleOrigin);
            }
            remainingObstacles--;
        }

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position

        moveBoxInPlanner(intention.targetBox, pls.getPosition(intention.goal), agentDestination); // add hlaction to plan, committedactions+1, update pls
        pls.revertLast(committedActions);
        return plan;
    }

    private void moveBoxInPlanner(Box box, Position boxDestination, Position agentDestination) {
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
