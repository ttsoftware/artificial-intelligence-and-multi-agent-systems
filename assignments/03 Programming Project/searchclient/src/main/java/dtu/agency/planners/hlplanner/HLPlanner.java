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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

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
        debug("",-20);

        HTNPlanner htnPlanner = new HTNPlanner( pls, idea, RelaxationMode.NoAgents );

        for (int i = 0; i < obstaclePositions.size() ; i++ ) {

            // identify the obstacle
            Position obstacleOrigin = obstaclePositions.pollFirst();
            Box box = new Box(pls.getObjectLabels(obstacleOrigin));

            if (idea.getBox() != box) { // not the goal box

                Iterator positions = freeNeighbours.getLast().iterator();
                Position target = (Position) positions.next();
                int currentDistance = obstacleOrigin.manhattanDist(target);
                boolean validTarget = false;

                HMoveBoxAction moveObstacle = null;

                while (!validTarget){

                    while (positions.hasNext()) {
                        Position next = (Position) positions.next();
                        int nextDistance = obstacleOrigin.manhattanDist(next);
                        if (nextDistance < currentDistance) {
                            target = next;
                            currentDistance = nextDistance;
                        }
                    }
                    HMoveBoxAction move = new HMoveBoxAction(box, target, obstacleOrigin);
                    htnPlanner.reload(move, RelaxationMode.NoAgents);
                    PrimitivePlan prim = htnPlanner.plan();

                    if (prim != null) { // it is possible to move the box to this target choose it
                        // Remove this target position from the free neighbours
                        freeNeighbours.getLast().remove(target);
                        if (freeNeighbours.getLast().isEmpty()) freeNeighbours.removeLast();
                        moveObstacle = move;
                        validTarget = true;
                    } // else loop

                    if (freeNeighbours.isEmpty()) {
                        // TODO if no more free neighbours - we are stuck! communication required
                        // communicate with agency - let go of goal / call in assistance / commit suicide / whatever
                    }
                }

                plan.append(moveObstacle);
                pls.apply(moveObstacle);
                committedActions += 1;


            } else { // next obstacle IS the goal box
                // count remaining obstacles, and if a path suficiently short exist around them take that
                // else move goalbox out of the way and replan from this state :-) RECURSIVELY
                debug("next obstacle is goal box");


//        (5. if target box is only movable box remaining:
//          - move it out of the path, to a neighbor cell close
//          - replan on HLPlan, reusing pls! states)

                HMoveBoxAction move = new HMoveBoxAction(
                        box,
                        pls.getPosition(idea.getGoal()),
                        pls.getPosition(idea.getGoal())
                );

                htnPlanner.reload(move, RelaxationMode.NoAgents);
                if (htnPlanner.plan()!=null) {
                    plan.append(move);
                    pls.apply(move);
                    committedActions += 1;
                }
                else{
                    debug("HALLO!!!! programmer det nu rekursivt :-)");
                    // TODO: move the box AND calculate new pseudo path -  before trying again.
                    return plan();
                }
            }
        }

        debug("",-2);
        // restore pls to the state before planning
        pls.revertLast(committedActions);
        return plan;

    }

}
