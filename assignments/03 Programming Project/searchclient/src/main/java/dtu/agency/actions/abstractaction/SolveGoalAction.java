package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.io.Serializable;

public class SolveGoalAction extends AbstractAction implements Serializable {

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

    public Goal getGoal() { return goal; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoal;
    }

    public Position getAgentDestination(PlanningLevelService pls) {
        return pls.getPosition(BDIService.getInstance().getAgent());
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
        System.err.println("agent: " + pls.getPosition(agent));
        System.err.println("box: " + boxOrigin);
        approximateSteps += pls.getPosition(agent).manhattanDist(boxOrigin) -1;
        approximateSteps += boxOrigin.manhattanDist(boxDestination);
        if (boxOrigin.isAdjacentTo(boxDestination)) {
            // ensures that boxes next to the goal is preferred eg in SAD2
            // this mirrors the elimination of risks of other boxes being in this part of the path
            approximateSteps -= 1;
        }
        return approximateSteps;
    }

    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        String s = "SolveGoalAction(" +
                getBox().toString() +
                "," +
                getGoal().toString() +
                ")";
        return s;
    }
}
