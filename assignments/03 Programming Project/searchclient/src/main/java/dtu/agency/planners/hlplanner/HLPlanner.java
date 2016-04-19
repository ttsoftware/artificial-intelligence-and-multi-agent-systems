package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.planners.htn.RelaxationMode;
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
    private SolveGoalAction idea;
    private PrimitivePlan pseudoPlan;
    private HLPlan plan;
    private int committedActions;

    /**
     *
     * @param idea SolveGoalAction deciding which box and which goal to target
     * @param pls
     */
    public HLPlanner(SolveGoalAction idea, PrimitivePlan relaxedPlan, PlanningLevelService pls) {
        this.pls  = pls;
        this.idea = idea;
        this.pseudoPlan = relaxedPlan;
        this.plan = new HLPlan();
        this.committedActions = 0;
    }


    public HLPlan plan() {
        debug("hl planning",2);
        debug("hl planning! ",20);
//            1. plan for solving goal relaxed (pseudo plan)
//            1a. obtain path cells
        LinkedList<Position> pseudoPath = pls.getOrderedPath(pseudoPlan);
        debug("pseudo path: " + pseudoPath);
//            1b. obtain agent destination
        Position agentDestination = pseudoPath.peekLast();
        debug("agent destination: " + agentDestination);
//        2. identify occupied board cells (obstacles) in the pseudo plan
        LinkedList<Position> obstaclePositions = pls.getObstaclePositions(pseudoPath);
        debug("obstacle positions (ordered): " + obstaclePositions);
//        3. identify as many free neighbouring cells as obstacles,
//         - this is organized in levels/rings from path, so that one
//           can start by moving boxes to the outer 'rings'
        Set<Position> path = new HashSet<Position>(pseudoPath);
        path.add(pls.getPosition(idea.getGoal()));
        path.add(pls.getPosition(idea.getBox()));

        LinkedList<HashSet<Position>> freeNeighbours = pls.getFreeNeighbours(path, obstaclePositions.size());

//        4. try and move boxes one by one to outer rings,
//         - will have to detect unavailable paths to free cells (HOW?)
//         - while storing the change in positions to pls
        debug("obs pos size: " +obstaclePositions.size());
        debug("",-20);

        HTNPlanner htnPlanner = new HTNPlanner( pls, idea, RelaxationMode.NoAgents );
        while (0 < obstaclePositions.size()) {

            // identify the obstacle
            Position obstacleOrigin = obstaclePositions.pollFirst();
            Box box = new Box(pls.getObjectLabels(obstacleOrigin));
            debug("obstacle: "+box+"@" + obstacleOrigin + " - goal box is " + idea.getBox(),20);
            debug("",-20);

            if (!idea.getBox().equals(box)) { // not the goal box
                debug("obstacle not goal box", 20);
                debug("", -20);

                // TODO: copy freeNeighbours before removing them... or keep them in another ds until reestablishing
                LinkedList<Position> neighboursAtLevel = new LinkedList<>(freeNeighbours.peekLast());
                LinkedList<HashSet<Position>> triedNeighbours = new LinkedList<>();
                triedNeighbours.addFirst(freeNeighbours.pollLast());

                Position target = neighboursAtLevel.peek();
                int currentDistance = obstacleOrigin.manhattanDist(target);
                boolean validTarget = false;

                HMoveBoxAction moveObstacle = null;

                while (!validTarget) { // TODO: fix infinite loop

                    Iterator neighbourIterator = neighboursAtLevel.iterator();
                    while (neighbourIterator.hasNext()) {
                        Position next = (Position) neighbourIterator.next();
                        int nextDistance = obstacleOrigin.manhattanDist(next);
                        if (nextDistance < currentDistance) {
                            target = next;
                            currentDistance = nextDistance;
                        }
                    }
                    debug("target: " + target, 20);
                    debug("", -20);

                    neighboursAtLevel.remove(target);
                    currentDistance = Integer.MAX_VALUE;
                    while (neighboursAtLevel.isEmpty()) {
                        if (freeNeighbours.isEmpty()){
                            // TODO if no more free neighbours - we are stuck! communication required
                            // communicate with agency - let go of goal / call in assistance / commit suicide / whatever
                            debug("no place to put box " + box + "@" + obstacleOrigin);
                            break;
                        }
                        neighboursAtLevel = new LinkedList<>(freeNeighbours.peekLast());
                        triedNeighbours.addFirst(freeNeighbours.pollLast());
                    }
                    debug("remaining free neighbours on this level: " + neighboursAtLevel, 20);
                    debug("", -20);


                    HMoveBoxAction move = new HMoveBoxAction(box, target, obstacleOrigin);
                    htnPlanner.reload(move, RelaxationMode.NoAgents);
                    PrimitivePlan prim = htnPlanner.plan();

                    if (prim != null) { // it is possible to move the box to this target choose it
                        debug("found a primitive plan to "+target, 20);
                        debug("", -20);
                        // Remove this target position from the free neighbours
                        // re establish free neighbours without 'target'
                        triedNeighbours.getFirst().remove(target);
                        while (!triedNeighbours.isEmpty()){
                            freeNeighbours.addLast(triedNeighbours.pollFirst());
                        }
                        if (freeNeighbours.getLast().isEmpty()) freeNeighbours.removeLast();
                        moveObstacle = move;
                        validTarget = true;
                    } else {
                        debug("target unreachable",20);
                        debug("",-20);
                    }

                }

                debug("appending to plan and applying to pls: "+moveObstacle, 20);
                debug("", -20);
                plan.append(moveObstacle);
                pls.apply(moveObstacle);
                committedActions += 1;

            } else { // next obstacle IS the goal box
                // TODO: count remaining obstacles, and if a path suficiently short exist around them take that
                // else move goalbox out of the way and replan from this state :-) RECURSIVELY
                obstaclePositions.addFirst(obstacleOrigin);
                debug("obstacle is goal box - breaking of", 20);
                debug("", -20);
                break;
            }
        }

        debug("plan before finding goal box: " + plan.toString(), 20);
        debug("", -20);

        Position goalBoxOrigin = obstaclePositions.pollFirst();

        HMoveBoxAction moveToGoal = new HMoveBoxAction(
                idea.getBox(),
                pls.getPosition(idea.getGoal()),
                agentDestination
        );


        // investigate if this is possible, if so do it
        if ( obstaclePositions.size() > 0) { // still obstacles in the way
//        (5. if target box is only movable box remaining:
//          - move it out of the path, to a neighbor cell close
//          - replan on HLPlan, reusing pls! states)

            // investigate WHERE to move this box before replanning and replan
            HMoveBoxAction moveOutOfPath = null;
            PrimitivePlan primitives = null;
            Iterator neighbours = freeNeighbours.getLast().iterator();

            while (primitives == null) {
                if (!neighbours.hasNext()) {
                    freeNeighbours.removeLast();
                    if (freeNeighbours.isEmpty()) break;
                }
                Position boxIntermediateDestination = (Position) neighbours.next();
                moveOutOfPath = new HMoveBoxAction(
                        idea.getBox(),
                        pls.getPosition(idea.getGoal()),
                        goalBoxOrigin
                );

                htnPlanner.reload(moveOutOfPath, RelaxationMode.NoAgents);
                primitives = htnPlanner.plan();
            }

            if (primitives != null) {
                plan.append(moveOutOfPath);
                pls.apply(moveOutOfPath);
                committedActions += 1;
            } else {
                // TODO: what then??
                debug("we cannot find a place to move the goal box to, and it is in the way...");
            }

            // Calculate new pseudo path -  before trying again - recursively.
            htnPlanner.reload(moveToGoal, RelaxationMode.NoAgentsNoBoxes);
            pseudoPlan = htnPlanner.plan();
            return plan();

        } else { // path to goal is supposedly free

            plan.append(moveToGoal);
            pls.apply(moveToGoal);
            committedActions += 1;

        }


        debug("",-2);
        // restore pls to the state before planning
        pls.revertLast(committedActions);
        return plan;

    }

}
