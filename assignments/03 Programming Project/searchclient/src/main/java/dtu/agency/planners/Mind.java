package dtu.agency.planners;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * This Planner creates the entire list of high level actions, that may get the job done.
 */
public class Mind {
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }

    private final Goal goal;
    private final PlanningLevelService pls;
    private final Ideas thoughts;        // list of all possible ideas of (pre-)plans to solve the goal

    /**
     * Constructor: All the Mind needs is the next goal and the beliefs of the agent solving it...
     */
    public Mind(Goal target, PlanningLevelService pls) {
        debug("Mind initializing...", 2);
        this.goal = target;
        this.pls = pls;
        this.thoughts = think();
        debug(".. Done Thinking (getting ideas)", -2);
    }

    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     */
    private Ideas think() {
        debug("think(): ", 2);
        Ideas ideas = new Ideas(goal, pls); // agent destination is used for heuristic purpose

        for (Box box : pls.getLevel().getBoxes()) {
            debug(box.getLabel().substring(0,1).toLowerCase() + "=?" + goal.getLabel().toLowerCase().substring(0,1),2);
            if (box.getLabel().toLowerCase().substring(0,1).equals(goal.getLabel().toLowerCase().substring(0,1))) {
                SolveGoalAction solveGoalAction = new SolveGoalAction(box, goal);
                ideas.add(solveGoalAction);
                debug("yes! -> adding" + solveGoalAction.toString(),-2);
            } else {
                debug("no!",-2);
            }
        }
        if (DebugService.inDebugMode()) {
            String s = "Ideas conceived:";
            ArrayList<AbstractAction> actions = new ArrayList<>(ideas.getIdeas());
            for (AbstractAction action : actions) {
                s += "\n" + action.toString();
            }
            debug(s, -2);
        }
        return ideas;
    }

    public Ideas getAllAbstractIdeas() {
        return thoughts;
    }

    public int getBestApproximateDistance() {
        return thoughts.peekBest().approximateSteps(pls);
    }

}

