package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.GoalIntention;
import dtu.agency.agent.bdi.Intention;
import dtu.agency.agent.bdi.MoveBoxFromPathIntention;
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
    private final Intention intention;
    private final HLPlan plan;

    /**
     * @param intention
     * @param pls
     */
    public HLPlanner(Intention intention, PlanningLevelService pls) {
        this.pls = pls;
        this.intention = intention;
        this.plan = new HLPlan();
    }

    public HLPlan plan() {

        if (intention == null) {
            throw new RuntimeException("How can the intention be null?");
        }

        if (intention instanceof GoalIntention) {
            return planGoalIntention((GoalIntention) intention);
        }
        if (intention instanceof MoveBoxFromPathIntention) {
            return planMoveBoxFromPathIntention((MoveBoxFromPathIntention) intention);
        }

        throw new RuntimeException("Unsupported intention: " + intention.getClass().getName());
    }

    private HLPlan planMoveBoxFromPathIntention(MoveBoxFromPathIntention moveBoxFromPathIntention) {

        ListIterator obstacles = moveBoxFromPathIntention.getObstaclePositions().listIterator();
        int remainingObstacles = moveBoxFromPathIntention.getObstacleCount();
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
                // we are already helping someone.
                // we must fail
                throw new RuntimeException("We (" + BDIService.getInstance().getAgent() + ") are already helping someone, so who should help us?");
            }

            if (box.equals(moveBoxFromPathIntention.getTargetBox()) && obstacles.hasNext()) {
                // move goal box to free position and try re-planning from there (recurse once)
                Position neighbour = pls.getFreeNeighbour(
                        moveBoxFromPathIntention.getCombinedPath(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
                moveBoxFromPathIntention.getObstaclePositions().removeAll(removedObstacles);

                System.err.println("RECURSION");
                return planMoveBoxFromPathIntention(moveBoxFromPathIntention); // Recursive behavior max depth is 1 :-)
            } else if (box.equals(moveBoxFromPathIntention.getTargetBox())) {
                // only 'obstacle' left in path is target box - move it into a free neighbour position

                Position neighbour = pls.getFreeNeighbour(
                        moveBoxFromPathIntention.getCombinedPath(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );

                moveBoxInPlanner(
                        box,
                        neighbour,
                        moveBoxFromPathIntention.getAgentPseudoPath().peekLast()
                );
                removedObstacles.add(obstacleOrigin);
                return plan;
            }
            else {
                // next obstacle is in the path - move box to free position
                Position neighbour = pls.getFreeNeighbour(
                        moveBoxFromPathIntention.getCombinedPath(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );

                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
            }
            remainingObstacles--;
        }

        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position obstacleOrigin = pls.getPosition(moveBoxFromPathIntention.getTargetBox());

        Position neighbourForBox = pls.getFreeNeighbour(
                moveBoxFromPathIntention.getCombinedPath(),
                agentPosition,
                obstacleOrigin,
                remainingObstacles+1 // we also need a space for the agent
        );

        Position neighbourForAgent = pls.getFreeNeighbour(
                moveBoxFromPathIntention.getCombinedPath(),
                agentPosition,
                obstacleOrigin,
                remainingObstacles
        );

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position
        moveBoxInPlanner(
                moveBoxFromPathIntention.getTargetBox(),
                neighbourForBox,
                neighbourForAgent
        );

        return plan;
    }

    private HLPlan planGoalIntention(GoalIntention goalIntention) {

        ListIterator obstacles = goalIntention.getObstaclePositions().listIterator();
        int remainingObstacles = goalIntention.getObstacleCount();
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

                System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": I need help moving obstacle: "  + box);

                HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                        goalIntention.getAgentBoxPseudoPathClone(),
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

            if (box.equals(goalIntention.getTargetBox()) && obstacles.hasNext()) {
                // move goal box to free position and try re-planning from there (recurse once)
                Position neighbour = pls.getFreeNeighbour(
                        goalIntention.getAgentBoxPseudoPathClone(),
                        agentPosition,
                        obstacleOrigin,
                        remainingObstacles
                );
                moveBoxInPlanner(box, neighbour, obstacleOrigin);
                removedObstacles.add(obstacleOrigin);
                goalIntention.getObstaclePositions().removeAll(removedObstacles);

                System.err.println("RECURSION");
                return planGoalIntention(goalIntention); // Recursive behavior max depth is 1 :-)
            } else if (box.equals(goalIntention.getTargetBox())) {
                // only 'obstacle' left in path is goal box - move it into goal position
                moveBoxInPlanner(
                        box,
                        pls.getPosition(goalIntention.getGoal()),
                        intention.getAgentPseudoPath().peekLast()
                );
                removedObstacles.add(obstacleOrigin);
                return plan;
            } else {
                // next obstacle is in the path - move box to free position
                Position neighbour = pls.getFreeNeighbour(
                        goalIntention.getAgentBoxPseudoPathClone(),
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
                goalIntention.getTargetBox(),
                pls.getPosition(goalIntention.getGoal()),
                goalIntention.getAgentPseudoPath().peekLast()
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
