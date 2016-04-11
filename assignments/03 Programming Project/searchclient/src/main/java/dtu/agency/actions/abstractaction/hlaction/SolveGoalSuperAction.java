package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.GlobalLevelService;

import java.io.Serializable;

public class SolveGoalSuperAction extends HLAction implements Serializable {

    private final Goal goal;

    public SolveGoalSuperAction(Goal goal) throws AssertionError {
        this.goal = goal;
        if (this.goal == null) {
            throw new AssertionError("SolveGoalSuperAction: null values not accepted for goal");
        }
    }

    public SolveGoalSuperAction(SolveGoalSuperAction other) {
        super();
        this.goal = new Goal(other.getGoal());
    }

    public Goal getGoal() { return goal; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoalSuper;
    }

    @Override
    public Position getDestination() {
        return GlobalLevelService.getInstance().getPosition(goal);
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

}
