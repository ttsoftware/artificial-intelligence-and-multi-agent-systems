package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;

import java.io.Serializable;

public class SolveGoalAction extends HLAction implements Serializable {

    private final Box box;
    private final Goal goal;

    public SolveGoalAction(Box box, Goal goal) throws AssertionError {
        this.box = box;
        this.goal = goal;
        if (this.box == null || this.goal == null) {
            throw new AssertionError("SolveGoalAction: null values not accepted for box or goal");
        }
    }

    public Goal getGoal() { return goal; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoal;
    }

    @Override
    public Position getDestination() {
        return this.goal.getPosition();
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("SolveGoalAction(");
        s.append(getBox().toString());
        s.append(",");
        s.append(getGoal().toString());
        s.append(")");
        return s.toString();
    }
}
