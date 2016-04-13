package dtu.agency.planners;

import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

/**
 * This Planner creates the entire list of high level actions, that may get the job done.
 */
public class Mind {

    Goal goal;
    PlanningLevelService pls;
    Ideas thoughts;        // list of all possible ideas of (pre-)plans to solve the goal

    /**
     * Constructor: All the Mind needs is the next goal and the beliefs of the agent solving it...
     */
    public Mind(Goal target, PlanningLevelService pls) {
//        debug("Mind initializing.",2);
        this.goal = target;
        this.pls = pls;
        this.thoughts = think();
//        debug("Ideas/Desires: " + thoughts.toString(),-2);
    }

    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     */
    public Ideas think() {
//        debug("HTNGoalPlanner.createAllNodes(): ", 2);
        Position boxDestination  = pls.getPosition(goal);
        Position agentDestination = pls.getAgentPosition(); // return, where else to go?? :-) null may work

        Ideas ideas = new Ideas(goal, agentDestination);

        for (Box box : pls.getLevel().getBoxes()) {
            if (box.getLabel().toLowerCase().equals(goal.getLabel().toLowerCase())) {
                HLAction action = new HMoveBoxAction( box, boxDestination, agentDestination );
                ideas.add(action);
                HLAction superAction = new SolveGoalAction(box, goal);
                ideas.add(superAction);
            }
        }
//        debug("Nodes created: \n" + String.join("\n", ideas.toString()) , -2);
        return ideas;
    }

    public Ideas getAllAbstractIdeas() {
        return thoughts;
    }

    public int getApproximateDistance() {
        return thoughts.getBest().approximateSteps(pls.getAgentPosition());
    }

}

