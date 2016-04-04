package dtu.agency.planners.actions;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;
import java.util.ArrayList;

public class SolveGoalAction extends HLAction implements Serializable {

    private final Box targetBox;
    private final Goal targetGoal;

    public SolveGoalAction(Box box, Goal goal) throws AssertionError {
        this.targetBox = box;
        this.targetGoal = goal;
        if (targetBox == null || targetGoal == null) {
            throw new AssertionError("SolveGoalAction: null values not accepted for box or goal");
        }
    }

    public Box getTargetBox() {
        return targetBox;
    }

    public Goal getTargetGoal() { return targetGoal; }

    @Override
    public Position getDestination() {
        return this.targetGoal.getPosition();
    }

    @Override
    public boolean isPureHLAction() { return true; }

    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        return htnState.getBoxPosition().equals(this.getTargetGoal().getPosition());
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
        // check if the prior state fulfills this HLActions target, and if so return empty plan of refinements
        // System.err.println("SolveGoalAction.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        MixedPlan refinement = new MixedPlan();
        refinement.addAction(new GotoAction(targetBox));
        refinement.addAction(new MoveBoxAction(targetBox, targetGoal));
        refinements.add(refinement);

        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("SolveGoalAction(");
        s.append(getTargetBox().toString());
        s.append(",");
        s.append(getTargetGoal().toString());
        s.append(")");
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolveGoalAction other = (SolveGoalAction) obj;
        if (!this.getTargetBox().equals(other.getTargetBox()))
            return false;
        if (!this.getTargetGoal().equals(other.getTargetGoal()))
            return false;
        return true;
    }
}
