package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.GoalIntention;
import dtu.agency.board.*;
import dtu.agency.events.agent.HelpMoveObstacleEvent;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.PlanningLevelService;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class HLPlanner {
    private final PlanningLevelService pls;
    private final GoalIntention intention;
    private final HLPlan plan;

    /**
     * @param intention
     * @param pls
     */
    public HLPlanner(GoalIntention intention, PlanningLevelService pls) {
        this.pls = pls;
        this.intention = intention;
        this.plan = new HLPlan();
    }

    public HLPlan plan() {

        if (intention == null) {
            throw new RuntimeException("How can the intention be null?");
        }

        ListIterator obstacles = intention.getObstaclePositions().listIterator();
        int remainingObstacles = intention.getObstacleCount();
        LinkedList<Position> removedObstacles = new LinkedList<>();

        while (obstacles.hasNext()) {
            Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());

            Position obstacleOrigin = (Position) obstacles.next();
            BoardObject boardObject = pls.getObject(obstacleOrigin);

            Box box;
            if (boardObject.getType() == BoardCell.BOX_GOAL) {
                // there is a box in our path
                BoxAndGoal boxGoal = ((BoxAndGoal) boardObject);
                if (boxGoal.isSolved()) {
                    throw new RuntimeException("I cannot un-solve a solved goal");
                } else {
                    box = ((BoxAndGoal) boardObject).getBox();
                }
            } else if (boardObject.getType() == BoardCell.BOX) {
                // there is a box in our path
                box = (Box) boardObject;
            } else if (boardObject.getType() == BoardCell.AGENT_GOAL
                    || boardObject.getType() == BoardCell.AGENT) {
                // there is an agent in our path - ask it to move
                // ignore it for now, it might move on its own
                // if it does not move on its own, we are gonna have to ask it to move
                remainingObstacles--;
                continue;
            } else {
                throw new AssertionError("What boardObject is this?: " + boardObject.getClass().getName());
            }

            // see if this agent can actually move this box/obstacle
            if (!box.getColor().equals(BDIService.getInstance().getAgent().getColor())) {
                // we need help
                HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                        intention.getAgentBoxPseudoPathClone(),
                        box
                );
                EventBusService.post(helpMeEvent);

                // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
                List<LinkedList<Position>> failedPaths = helpMeEvent.getResponse();

                for (LinkedList<Position> failedPath : failedPaths) {
                    // failedPath is a path to move this obstacle, which has obstacles of its own
                    // TODO: Routine for moving obstacle out of this path
                }

                remainingObstacles--;
                continue;
            }

            if (box.equals(intention.getTargetBox()) && obstacles.hasNext()) {
                // move goal box to free position and try re-planning from there (recurse once)
                Position neighbour = pls.getFreeNeighbour(
                        intention.getAgentBoxPseudoPathClone(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
                intention.getObstaclePositions().removeAll(removedObstacles);

                System.err.println("RECURSION");
                return plan(); // Recursive behavior max depth is 1 :-)
            } else if (box.equals(intention.getTargetBox())) {
                // only 'obstacle' left in path is goal box - move it into goal position
                moveBoxInPlanner(
                        box,
                        pls.getPosition(intention.getGoal()),
                        intention.getAgentPseudoPath().peekLast()
                );
                removedObstacles.add(obstacleOrigin);
                return plan;
            } else {
                // next obstacle is in the path - move box to free position
                Position neighbour = pls.getFreeNeighbour(
                        intention.getAgentBoxPseudoPathClone(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );

                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
            }
            remainingObstacles--;
        }

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position
        moveBoxInPlanner(
                intention.getTargetBox(),
                pls.getPosition(intention.getGoal()),
                intention.getAgentPseudoPath().peekLast()
        );

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
    }
}
