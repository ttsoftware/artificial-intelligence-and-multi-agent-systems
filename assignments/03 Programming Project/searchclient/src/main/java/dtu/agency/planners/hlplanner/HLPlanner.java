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

    /**
     * Recursively generate a high-level plan
     *
     * @param intention
     * @param plan
     * @return
     */
    public HLPlan plan(Intention intention,
                       HLPlan plan) {

        List<Position> obstacles = intention.getObstaclePositionsClone();
        int remainingObstacles = obstacles.size();

        if (remainingObstacles == 0) {
            plan = addLastAction(intention, plan, remainingObstacles);

            return plan;
        }
        else {
            // solve obstacles

            Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
            Position obstaclePosition = obstacles.get(0);
            BoardObject boardObject = pls.getObject(obstaclePosition);

            if (boardObject.getType() == BoardCell.AGENT_GOAL
                    || boardObject.getType() == BoardCell.AGENT) {
                // there is an agent in our path - ask it to move
                // ignore it for now, it might move on its own
                // if it does not move on its own, we are gonna have to ask it to move
            } else {
                Box box = extractBox(boardObject);

                // see if this agent can actually move this box/obstacle
                if (!box.getColor().equals(BDIService.getInstance().getAgent().getColor())) {
                    // should this update the plan?
                    plan = helpMoveObstacle(box);
                } else {
                    plan = moveObstacle(
                            agentPosition,
                            obstaclePosition,
                            box,
                            remainingObstacles--,
                            plan
                    );
                }
            }

            // recursively solve next obstacle
            return plan(intention, plan);
        }
    }

    private HLPlan addLastAction(Intention intention,
                                 HLPlan plan,
                                 int remainingObstacles) {

        if (intention instanceof GoalIntention) {
            // ask for help
            return addLastAction((GoalIntention) intention, plan, remainingObstacles);
        }
        if (intention instanceof MoveBoxFromPathIntention) {
            // we need help we are already helping someone. We must fail
            return addLastAction((MoveBoxFromPathIntention) intention, plan, remainingObstacles);
        }

        throw new RuntimeException("Invalid intention");
    }

    private HLPlan addLastAction(GoalIntention intention,
                                 HLPlan plan,
                                 int remainingObstacles) {

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position
        return moveBoxInPlanner(
                intention.getTargetBox(),
                pls.getPosition(intention.getGoal()),
                intention.getAgentPseudoPathClone().peekLast(),
                plan
        );
    }

    private HLPlan addLastAction(MoveBoxFromPathIntention intention,
                                 HLPlan plan,
                                 int remainingObstacles) {

        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position obstacleOrigin = pls.getPosition(intention.getTargetBox());

        Position neighbourForBox = pls.getFreeNeighbour(
                intention.getCombinedPath(),
                agentPosition,
                obstacleOrigin,
                remainingObstacles + 1 // we also need a space for the agent
        );

        Position neighbourForAgent = pls.getFreeNeighbour(
                intention.getCombinedPath(),
                agentPosition,
                obstacleOrigin,
                remainingObstacles
        );

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position
        return moveBoxInPlanner(
                intention.getTargetBox(),
                neighbourForBox,
                neighbourForAgent,
                plan
        );
    }

    private HLPlan helpMoveObstacle(Box box) {

        if (intention instanceof GoalIntention) {
            // ask for help
            // TODO: can we go around ??
            System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": I need help moving obstacle: " + box);

            HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                    intention.getAgentBoxPseudoPathClone(),
                    box
            );
            EventBusService.post(helpMeEvent);

            // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
            List<LinkedList<Position>> failedPaths = helpMeEvent.getResponse();

            // find first obstacle in our original path
            Position firstObstaclePosition = intention.getObstaclePositions().peekFirst();
            Box firstObstacle = (Box) pls.getObject(firstObstaclePosition);

            for (LinkedList<Position> failedPath : failedPaths) {
                // failedPath is a path to move this obstacle, which has obstacles of its own
                if (failedPath.contains(firstObstaclePosition)) {
                    // we can help this path by moving firstObstacle out of it
                    boolean successful = BDIService.getInstance().findMoveBoxFromPathIntention(
                            failedPath,
                            firstObstacle
                    );
                    if (successful) {
                        // Create plan for moving obstacle
                        successful &= BDIService.getInstance().solveMoveBox(failedPath, firstObstacle);

                        if (successful) {
                            // retrieve the list of primitive actions to execute (blindly)
                            return BDIService.getInstance().getCurrentHLPlan();
                        }
                    }
                }
            }
        }

        if (intention instanceof MoveBoxFromPathIntention) {
            // we need help we are already helping someone. We must fail
            throw new RuntimeException("We (" + BDIService.getInstance().getAgent() + ") are already helping someone, so who should help us?");
        }

        throw new RuntimeException("invalid intention");
    }

    private HLPlan moveObstacle(Position agentPosition,
                                Position obstaclePosition,
                                Box box,
                                int remainingObstacles,
                                HLPlan plan) {

        LinkedList<Position> intentionPath = null;
        LinkedList<Position> intentionPathIncludingBox = null;
        Position obstacleGoalPosition = null;

        if (intention instanceof GoalIntention) {
            // ask for help
            intentionPath = intention.getAgentPseudoPathClone();
            intentionPathIncludingBox = intention.getAgentBoxPseudoPathClone();
            obstacleGoalPosition = pls.getPosition(((GoalIntention) intention).getGoal());
        }
        if (intention instanceof MoveBoxFromPathIntention) {
            // we need help we are already helping someone. We must fail
            intentionPath = ((MoveBoxFromPathIntention) intention).getCombinedPath();
            intentionPathIncludingBox = ((MoveBoxFromPathIntention) intention).getCombinedPath();

            obstacleGoalPosition = pls.getFreeNeighbour(
                    intentionPath,
                    agentPosition,
                    obstaclePosition,
                    remainingObstacles
            );
        }

        if (box.equals(intention.getTargetBox()) && remainingObstacles > 1) {
            // move goal box to free position and try re-planning from there (recurse once)
            Position neighbour = pls.getFreeNeighbour(
                    intentionPathIncludingBox,
                    agentPosition,
                    obstaclePosition,
                    remainingObstacles
            );
            plan = moveBoxInPlanner(box, neighbour, obstaclePosition, plan);
            intention.getObstaclePositions().remove(obstaclePosition);

            System.err.println("RECURSION");
            // recursively re-plan
            return plan(intention, plan);
        } else if (box.equals(intention.getTargetBox())) {
            // only 'obstacle' left in path is target box - move it into a free neighbour position
            plan = moveBoxInPlanner(
                    box,
                    obstacleGoalPosition,
                    intentionPath.peekLast(),
                    plan
            );
        } else {
            // next obstacle is in the path - move box to free position
            Position neighbour = pls.getFreeNeighbour(
                    intentionPathIncludingBox,
                    agentPosition,
                    obstaclePosition,
                    remainingObstacles
            );

            plan = moveBoxInPlanner(box, neighbour, obstaclePosition, plan);
        }

        intention.getObstaclePositions().remove(obstaclePosition);
        return plan;
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

            if (boardObject.getType() == BoardCell.AGENT_GOAL
                    || boardObject.getType() == BoardCell.AGENT) {
                // there is an agent in our path - ask it to move
                // ignore it for now, it might move on its own
                // if it does not move on its own, we are gonna have to ask it to move
                remainingObstacles--;
            } else {
                Box box = extractBox(boardObject);

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
                            moveBoxFromPathIntention.getAgentPseudoPathClone().peekLast()
                    );
                    removedObstacles.add(obstacleOrigin);
                    return plan;
                } else {
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
        }

        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position obstacleOrigin = pls.getPosition(moveBoxFromPathIntention.getTargetBox());

        Position neighbourForBox = pls.getFreeNeighbour(
                moveBoxFromPathIntention.getCombinedPath(),
                agentPosition,
                obstacleOrigin,
                remainingObstacles + 1 // we also need a space for the agent
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

        ListIterator<Position> obstacles = goalIntention.getObstaclePositions().listIterator();
        int remainingObstacles = goalIntention.getObstacleCount();
        LinkedList<Position> removedObstacles = new LinkedList<>();

        while (obstacles.hasNext()) {
            Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());

            Position obstacleOrigin = (Position) obstacles.next();
            BoardObject boardObject = pls.getObject(obstacleOrigin);

            if (boardObject.getType() == BoardCell.AGENT_GOAL
                    || boardObject.getType() == BoardCell.AGENT) {
                // there is an agent in our path - ask it to move
                // ignore it for now, it might move on its own
                // if it does not move on its own, we are gonna have to ask it to move
                remainingObstacles--;
            } else {
                Box box = extractBox(boardObject);

                // see if this agent can actually move this box/obstacle
                if (!box.getColor().equals(BDIService.getInstance().getAgent().getColor())) {
                    // TODO: can we go around ??
                    // Not our color - we need help

                    System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": I need help moving obstacle: " + box);

                    HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                            goalIntention.getAgentBoxPseudoPathClone(),
                            box
                    );
                    EventBusService.post(helpMeEvent);

                    // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
                    List<LinkedList<Position>> failedPaths = helpMeEvent.getResponse();

                    // find first obstacle in our original path
                    Position firstObstaclePosition = goalIntention.getObstaclePositions().peekFirst();
                    Box firstObstacle = (Box) pls.getObject(firstObstaclePosition);

                    for (LinkedList<Position> failedPath : failedPaths) {
                        // failedPath is a path to move this obstacle, which has obstacles of its own
                        if (failedPath.contains(firstObstaclePosition)) {
                            // we can help this path by moving firstObstacle out of it
                            boolean successful = BDIService.getInstance().findMoveBoxFromPathIntention(
                                    failedPath,
                                    firstObstacle
                            );
                            if (successful) {
                                // Create plan for moving obstacle
                                successful &= BDIService.getInstance().solveMoveBox(failedPath, firstObstacle);

                                if (successful) {
                                    // retrieve the list of primitive actions to execute (blindly)
                                    return BDIService.getInstance().getCurrentHLPlan();
                                }
                            }
                        }
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
                            intention.getAgentPseudoPathClone().peekLast()
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
        }

        // no more obstacles - and the goal box was not among the obstacles
        // move it to the goal position
        moveBoxInPlanner(
                goalIntention.getTargetBox(),
                pls.getPosition(goalIntention.getGoal()),
                goalIntention.getAgentPseudoPathClone().peekLast()
        );

        return plan;
    }

    private Box extractBox(BoardObject boardObject) {
        if (boardObject.getType() == BoardCell.BOX_GOAL) {
            // there is a box in our path
            BoxAndGoal boxGoal = ((BoxAndGoal) boardObject);
            if (boxGoal.isSolved()) {
                // TODO: can we go around ??
                throw new RuntimeException("I cannot un-solve a solved goal");
            } else {
                return ((BoxAndGoal) boardObject).getBox();
            }
        }
        if (boardObject.getType() == BoardCell.BOX) {
            // there is a box in our path
            return (Box) boardObject;
        }

        throw new RuntimeException("BoardObject is not of type Box");
    }

    private void handleObstacle(Box box) {

    }

    private void handleObstacle(Agent agent) {

    }

    private void moveBoxInPlanner(Box box, Position boxDestination, Position agentDestination) {
        HMoveBoxAction moveBoxAction = new HMoveBoxAction(
                box,
                boxDestination,
                agentDestination
        );
        plan.append(moveBoxAction);
        pls.apply(moveBoxAction);
    }

    private HLPlan moveBoxInPlanner(Box box,
                                    Position boxDestination,
                                    Position agentDestination,
                                    HLPlan plan) {
        HMoveBoxAction moveBoxAction = new HMoveBoxAction(
                box,
                boxDestination,
                agentDestination
        );
        plan.append(moveBoxAction);
        pls.apply(moveBoxAction);

        return plan;
    }
}
