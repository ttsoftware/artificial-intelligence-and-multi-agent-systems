package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.GoalIntention;
import dtu.agency.agent.bdi.Intention;
import dtu.agency.agent.bdi.MoveBoxFromPathIntention;
import dtu.agency.board.*;
import dtu.agency.events.agent.HelpMoveObstacleEvent;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class HLPlanner {
    private final PlanningLevelService pls;
    private final Intention intention;

    /**
     * @param intention
     * @param pls
     */
    public HLPlanner(Intention intention, PlanningLevelService pls) {
        this.pls = pls;
        this.intention = intention;

        if (intention instanceof GoalIntention) {

            List<Position> obstacles = intention.getObstaclePositionsClone();
            int remainingObstacles = obstacles.size();

            if (remainingObstacles > 2) {
                // we want to remove the first obstacle ourselves
                obstacles.remove(0);

                List<Position> obstaclesClone = new ArrayList<>();
                obstacles.forEach(obstaclePosition -> obstaclesClone.add(new Position(obstaclePosition)));

                // obstacles to remove from the intention
                List<HelpMoveObstacleEvent> helpMoveObstaclesEvents = new ArrayList<>();

                // create stream
                obstaclesClone.forEach(obstaclePosition -> {
                    Box obstacle = extractBox(pls.getObject(obstaclePosition));

                    if (!obstacle.equals(intention.getTargetBox())) {
                        // we do not need help moving the goal box
                        helpMoveObstaclesEvents.add(helpMoveObstacle(obstacle));
                    }
                });

                List<Box> removedObstacles = new ArrayList<>();

                helpMoveObstaclesEvents.parallelStream().forEach(helpMeEvent -> {

                    // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
                    List<LinkedList<Position>> failedPaths = helpMeEvent.getResponse();

                    if (failedPaths.size() == 0) {
                        removedObstacles.add((Box) helpMeEvent.getObstacle());
                    }
                });

                if (removedObstacles.size() == 0) {
                    System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": No one could help me");
                } else {
                    removedObstacles.forEach(box -> {
                        intention.removeObstacle(pls.getPosition(box));
                    });

                    // update the BDI to reflect moved obstacles
                    BDIService.getInstance().updateBDILevelService();
                }
            }
        }
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
        } else {
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
                Box obstacle = extractBox(boardObject);

                // see if this agent can actually move this box/obstacle
                if (!obstacle.getColor().equals(BDIService.getInstance().getAgent().getColor())) {
                    // should this update the plan?
                    HLPlan helpMovePlan = helpMoveForeignObstacle(obstacle, obstaclePosition, plan);
                    if (helpMovePlan.isEmpty()) {
                        return new HLPlan();
                    }
                    return helpMovePlan;
                } else {
                    HLPlan appendPlan = moveObstacle(
                            agentPosition,
                            obstaclePosition,
                            obstacle,
                            remainingObstacles--,
                            plan
                    );

                    if (appendPlan.isEmpty()) {
                        return plan;
                    }

                    plan = appendPlan;
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

        Position targetBoxPosition = pls.getPosition(intention.getTargetBox());
        if (intention.getRemovedObstacles().contains(targetBoxPosition)) {
            return plan;
        }
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
        Position obstaclePosition = pls.getPosition(intention.getTargetBox());

        Position neighbourForBox = pls.getFreeNeighbour(
                intention.getCombinedPath(),
                agentPosition,
                obstaclePosition,
                remainingObstacles + 2 // we also need a space for the agent
        );

        Position neighbourForAgent = pls.getFreeNeighbour(
                intention.getCombinedPath(),
                agentPosition,
                obstaclePosition,
                remainingObstacles + 1
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

    private HelpMoveObstacleEvent helpMoveObstacle(Box obstacle) {
        if (intention instanceof GoalIntention) {
            System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": I need help moving obstacle: " + obstacle);

            HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                    BDIService.getInstance().getAgent(),
                    intention.getAgentBoxPseudoPathClone(),
                    obstacle
            );
            EventBusService.post(helpMeEvent);

            return helpMeEvent;
        }

        throw new RuntimeException("invalid intention");
    }

    private HLPlan helpMoveForeignObstacle(Box obstacle, Position obstaclePosition, HLPlan plan) {

        if (intention instanceof GoalIntention) {
            // ask for help
            System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": I need help moving obstacle: " + obstacle);

            HelpMoveObstacleEvent helpMeEvent = new HelpMoveObstacleEvent(
                    BDIService.getInstance().getAgent(),
                    intention.getAgentBoxPseudoPathClone(),
                    obstacle
            );
            EventBusService.post(helpMeEvent);

            // wait until someone moved the obstacle - blocks this thread for at most 2^32-1 milliseconds
            List<LinkedList<Position>> failedPaths = helpMeEvent.getResponse();

            if (failedPaths.size() > 0) {
                System.err.println(Thread.currentThread().getName() + ": Agent: " + BDIService.getInstance().getAgent() + ": No-one could move my obstacle: " + obstacle);

                for (LinkedList<Position> failedPath : failedPaths) {
                    // failedPath is a path to move the foreign obstacle, which has obstacles of its own

                    LinkedList<Position> failedPathObstacles = pls.getObstaclePositions(failedPath);

                    for (Position failedPathObstaclePosition : failedPathObstacles) {
                        if (intention.getRemovedObstacles().contains(failedPathObstaclePosition)) {
                            // this obstacle has already been moved
                        } else {
                            // we can help this failed path by moving failedPathObstacle out of it
                            Box failedPathBox = (Box) pls.getObject(failedPathObstaclePosition);

                            if (failedPathBox == obstacle) {
                                // this obstacle is the one we could not move in the first pace
                                continue;
                            }

                            // lets try moving this obstacle
                            boolean successful = BDIService.getInstance().findMoveBoxFromPathIntention(
                                    failedPath,
                                    failedPathBox
                            );

                            if (successful) {
                                // Create plan for moving obstacle
                                try {
                                    successful &= BDIService.getInstance().solveMoveBox(failedPathBox);
                                } catch (RecursiveHelpException e) {
                                    // e.printStackTrace(System.err);
                                    // we cannot ask for help while asking for help
                                    successful = false;
                                }

                                if (successful) {
                                    // retrieve the list of primitive actions to execute (blindly)
                                    HLPlan movingFailedObstaclePlan = BDIService.getInstance().getCurrentHLPlan();
                                    // "remove" all remaining obstacles, in order to return this plan
                                    intention.getObstaclePositions().forEach(intention::removeObstacle);
                                    return movingFailedObstaclePlan;
                                } else {
                                    // we cannot move failedPathObstacle - it is probably a different color
                                }
                            } else {
                                // we cannot move failedPathObstacle - it is probably a different color
                            }
                        }
                    }
                }
                // we cannot move any failedPathObstacles - they are probably a different color
                // "remove" all remaining obstacles, in order to return this plan
                intention.getObstaclePositions().forEach(intention::removeObstacle);
                return plan;
            } else {
                // someone moved the obstacle!
                intention.removeObstacle(obstaclePosition);
                return new HLPlan();
            }
        }

        if (intention instanceof MoveBoxFromPathIntention) {
            // we need help we are already helping someone. We must fail
            throw new RecursiveHelpException("We (" + BDIService.getInstance().getAgent() + ") are already helping someone, so who should help us move " + obstacle + "?");
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

            try {
                obstacleGoalPosition = pls.getFreeNeighbour(
                        intentionPath,
                        agentPosition,
                        obstaclePosition,
                        remainingObstacles + 3
                );
            } catch (NoFreeNeighboursException e) {
                // No free neighbour, so we submit the plan we made so far
                intention.removeObstacle(obstaclePosition);
                return new HLPlan();
            }
        }

        if (box.equals(intention.getTargetBox()) && remainingObstacles > 1) {
            // move goal box to free position and try re-planning from there (recurse once)
            Position neighbour;
            try {
                neighbour = pls.getFreeNeighbour(
                        intentionPathIncludingBox,
                        agentPosition,
                        obstaclePosition,
                        remainingObstacles + 3
                );
            } catch (NoFreeNeighboursException e) {
                // No free neighbour, so we submit the plan we made so far
                intention.removeObstacle(obstaclePosition);
                return new HLPlan();
            }
            plan = moveBoxInPlanner(box, neighbour, obstaclePosition, plan);
            intention.removeObstacle(obstaclePosition);
            // finish this plan and re-estimate
            return plan;
        } else if (box.equals(intention.getTargetBox())) {
            // only 'obstacle' left in path is target box - move it into the goal
            if (pls.isFree(obstacleGoalPosition)
                    || pls.getCell(obstacleGoalPosition).equals(BoardCell.AGENT)
                    || pls.getCell(obstacleGoalPosition).equals(BoardCell.AGENT_GOAL)) {
                // the goal is free
                plan = moveBoxInPlanner(
                        box,
                        obstacleGoalPosition,
                        intentionPath.peekLast(),
                        plan
                );
            }
        } else {
            // next obstacle is in the path - move box to free position
            Position neighbour = null;
            try {
                neighbour = pls.getFreeNeighbour(
                        intentionPathIncludingBox,
                        agentPosition,
                        obstaclePosition,
                        remainingObstacles + 3
                );
            } catch (NoFreeNeighboursException e) {
                // No free neighbour, so we submit the plan we made so far
                intention.removeObstacle(obstaclePosition);
                return new HLPlan();
            }

            plan = moveBoxInPlanner(box, neighbour, obstaclePosition, plan);
        }

        intention.removeObstacle(obstaclePosition);
        return plan;
    }

    private Box extractBox(BoardObject boardObject) {
        if (boardObject.getType() == BoardCell.BOX_GOAL) {
            // there is a box in our path
            BoxAndGoal boxGoal = ((BoxAndGoal) boardObject);
            /*if (boxGoal.isSolved()) {
                throw new RuntimeException("I cannot un-solve a solved goal");
            } else {*/
                return ((BoxAndGoal) boardObject).getBox();
            //}
        }
        if (boardObject.getType() == BoardCell.BOX) {
            // there is a box in our path
            return (Box) boardObject;
        }

        throw new RuntimeException("BoardObject is not of type Box");
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
        try {
            pls.apply(moveBoxAction);
        } catch (NotAFreeCellException e) {
            return new HLPlan();
        }
        plan.append(moveBoxAction);

        return plan;
    }
}
