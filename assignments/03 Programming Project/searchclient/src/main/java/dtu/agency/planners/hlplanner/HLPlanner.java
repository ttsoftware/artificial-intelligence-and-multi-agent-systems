package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.util.LinkedList;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
class HLPlanner {

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
        if (ideaIsValid) {
//            1. plan for solving goal relaxed (pseudo plan)
//            1a. obtain agent destination
//            Position agentDestination = pls.getAgentDestination(pseudoPlan);
//            1b. obtain path cells
//            LinkedList<Position> pseudoPath = pls.getOrderedPath(pseudoPlan);
//        2. identify occupied board cells (obstacles) in the pseudo plan
//            LinkedList<Position> obstaclePositions = pls.getOccupiedPositions(pseudoPath);
//        3. identify as many free neighbouring cells as obstacles,
//         - this is organized in levels/rings from path, so that one
//           can start by moving boxes to the outer 'rings'
//            LinkedList<LinkedList<Position>> = pls.getFreeNeighbours(pseudoPath, obstaclePositions.size());

//        4. try and move boxes one by one to outer rings,
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
            return new HLPlan( translatedIdea );
        } else {
            System.err.println("HLPlanner: Invalid idea, there does not exist any path between box and goal");
            return new HLPlan();
        }

    }

}
