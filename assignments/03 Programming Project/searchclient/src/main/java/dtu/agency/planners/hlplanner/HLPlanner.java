package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;

import java.util.HashSet;
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
    private boolean ideaIsValid;

    /**
     *
     * @param idea SolveGoalAction deciding which box and which goal to target
     * @param pls
     */
    public HLPlanner(SolveGoalAction idea, PlanningLevelService pls) {
        this.pls  = pls;
        this.idea = idea;
        HTNPlanner htnPlanner = new HTNPlanner( new PlanningLevelService(pls), idea, RelaxationMode.NoAgentsNoBoxes );
        this.pseudoPlan  = htnPlanner.plan();
        this.ideaIsValid = pseudoPlan != null;
    }


    public HLPlan plan() {
        debug("",2);
        if (ideaIsValid) {
//            1. plan for solving goal relaxed (pseudo plan)
//            1a. obtain path cells
            LinkedList<Position> pseudoPath = pls.getOrderedPath(pseudoPlan);
//            1b. obtain agent destination
            Position agentDestination = pseudoPath.peekLast();
//        2. identify occupied board cells (obstacles) in the pseudo plan
            LinkedList<Position> obstaclePositions = pls.getObstaclePositions(pseudoPath);
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

//        (5. if target box is only movable box remaining:
//          - move it out of the path, to a neighbor cell close
//          - replan on HLPlan, reusing pls! states)

            // TODO: do some real high level planning
            // change this to return an ordered list of HLActions, which if  performed in this order
            // will solve the problem in topLevelIntention
            HMoveBoxAction translatedIdea = new HMoveBoxAction(
                    idea.getBox(),
                    idea.getBoxDestination(),
                    idea.getAgentDestination(pls)
            );
            debug("",-2);
            return new HLPlan( translatedIdea );
        } else {
            debug("HLPlanner: Invalid idea, there does not exist any path between box and goal",-2);
            return new HLPlan();
        }

    }

}
