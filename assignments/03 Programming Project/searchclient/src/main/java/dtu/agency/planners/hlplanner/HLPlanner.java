package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.*;
import dtu.agency.events.agent.HelpMoveObstacleEvent;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;
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

    /**
     * @param intention
     * @param pls
     */
    public HLPlanner(AgentIntention intention, PlanningLevelService pls) {
        this.pls = pls;
        this.intention = intention;
        this.plan = new HLPlan();
    }

    public HLPlan plan() {

        if (intention == null) {
            throw new RuntimeException("How can the intention be null?");
        }

        ListIterator obstacles = intention.obstaclePositions.listIterator();
        int remainingObstacles = intention.obstaclePositions.size();
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
                        intention.getAgentAndBoxPseudoPath(),
                        box
                );
                EventBusService.post(helpMeEvent);

                // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
                boolean isObstacleMoved = helpMeEvent.getResponse();

                if (isObstacleMoved) {
                    // we can continue to try solving next obstacle
                    continue;
                }
                else {
                    // TODO: No one could move this obstacle? :'(
                }
            }

            if (box.equals(intention.targetBox) && obstacles.hasNext()) {
                // move goal box to free position and try re-planning from there (recurse once)
                Position neighbour = pls.getFreeNeighbour(
                        intention.getAgentAndBoxPseudoPath(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
                intention.obstaclePositions.removeAll(removedObstacles);

                System.err.println("RECURSION");
                return plan(); // Recursive behavior max depth is 1 :-)
            } else if (box.equals(intention.targetBox)) {
                // only 'obstacle' left in path is goal box - move it into goal position
                moveBoxInPlanner(
                        box,
                        pls.getPosition(intention.goal),
                        intention.getAgentPseudoPath().peekLast()
                );
                removedObstacles.add(obstacleOrigin);
                return plan;
            } else {
                // next obstacle is in the path - move box to free position
                Position neighbour = pls.getFreeNeighbour(
                        intention.getAgentAndBoxPseudoPath(),
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
                intention.targetBox,
                pls.getPosition(intention.goal),
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
