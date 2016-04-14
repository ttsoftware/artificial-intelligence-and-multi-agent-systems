package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;
import dtu.agency.services.GlobalLevelService;

import java.io.Serializable;

/**
 * Intention are for this to perish
 */
public class SolveGoalSuperAction extends HLAction implements Serializable {

    private final Goal goal;

    public SolveGoalSuperAction(Goal goal) throws AssertionError {
        this.goal = goal;
        if (this.goal == null) {
            throw new AssertionError("SolveGoalSuperAction: null values not accepted for goal");
        }
    }

    public SolveGoalSuperAction(SolveGoalSuperAction other) {
        this.goal = new Goal(other.getGoal());
    }

    public Goal getGoal() { return goal; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoalSuper;
    }

    @Override
    public Position getAgentDestination() {
        return GlobalLevelService.getInstance().getPosition(goal);
    }

    @Override
    public Position getBoxDestination() {
        return BDIService.getInstance().getBDILevelService().getPosition(goal);
    }

    @Override
    public Box getBox() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("SolveGoalSuperAction(");
        s.append(getGoal().toString());
        s.append(")");
        return s.toString();
    }

    @Override
    public int approximateSteps(Position agentOrigin) {
        return Integer.MAX_VALUE;
    }

}
