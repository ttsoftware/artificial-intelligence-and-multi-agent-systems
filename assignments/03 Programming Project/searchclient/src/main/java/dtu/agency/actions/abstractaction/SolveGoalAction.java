package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

public class SolveGoalAction extends AbstractAction {

    private final Box box;
    private final Goal goal;

    public SolveGoalAction(Box box, Goal goal) throws AssertionError {
        this.box = box;
        this.goal = goal;
        if (this.box == null || this.goal == null) {
            throw new AssertionError("SolveGoalAction: null values not accepted for box or goal");
        }
    }

    public SolveGoalAction(SolveGoalAction other) {
        this.box = new Box(other.getBox());
        this.goal = new Goal(other.getGoal());
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoal;
    }

    public Position getAgentDestination(PlanningLevelService pls) {
        // TODO: Why does the agent always wish to go back to where it started from?
        return pls.getPosition(box);
    }

    public Position getBoxDestination() {
        return BDIService.getInstance().getBDILevelService().getPosition(goal);
    }

    public int approximateSteps(PlanningLevelService pls) {
        // this is gonna be a rough estimation, on the relaxed path
        Agent agent = BDIService.getInstance().getAgent();
        int approximateSteps = 0;

        Position boxOrigin = pls.getPosition(box);
        Position boxDestination = pls.getPosition(goal);


        if (boxOrigin.isAdjacentTo(boxDestination)) {
            // ensures that boxes next to the goal is preferred eg in SAD2
            // this mirrors the elimination of risks of other boxes being in this part of the path
            approximateSteps += -1;
        }
        else {
            approximateSteps += boxOrigin.manhattanDist(boxDestination);
        }
        approximateSteps += pls.getPosition(agent).manhattanDist(boxOrigin) - 1;

        return approximateSteps;
    }

    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        return "SolveGoalAction(" + getBox().toString() + "," + getGoal().toString() + ")";
    }
}
