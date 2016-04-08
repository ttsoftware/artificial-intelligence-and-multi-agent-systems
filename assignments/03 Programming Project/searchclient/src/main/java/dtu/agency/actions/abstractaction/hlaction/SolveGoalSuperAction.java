package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.services.GlobalLevelService;

import java.io.Serializable;
import java.util.ArrayList;

public class SolveGoalSuperAction extends HLAction implements Serializable {

    private final Box box;
    private final Goal goal;

    public SolveGoalSuperAction(Goal goal) throws AssertionError {
        this.box = null;
        this.goal = goal;
        if (this.goal == null) {
            throw new AssertionError("SolveGoalSuperAction: null values not accepted for goal");
        }
    }

    public Box getBox() {
        return box;
    }

    public Goal getGoal() { return goal; }

    @Override
    public Position getDestination() {
        return GlobalLevelService.getInstance().getPosition(goal);
    }

    @Override
    public boolean isPureHLAction() { return true; }

    @Override
    public boolean isPurposeFulfilled(HTNState state) {
//        System.err.println("SGSA.isPurposeFulfilled(): goal: " + goal.toString());
        return (state.getBoxPosition()!=null) ? state.getBoxPosition().equals(this.getGoal().getPosition()): false;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
        // System.err.println("SGSA.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        for (Box box : GlobalLevelService.getInstance().getLevel().getBoxes()) {
            if (box.getLabel().toLowerCase().equals(goal.getLabel().toLowerCase())) {
                MixedPlan refinement = new MixedPlan();
                refinement.addAction( new SolveGoalAction(box, goal) );
                refinements.add(refinement);
            }
        }
        //System.err.println("SGSA.getRefine(): "+ refinements.toString());
        return refinements;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolveGoalSuperAction other = (SolveGoalSuperAction) obj;
        if (!this.getBox().equals(other.getBox()))
            return false;
        if (!this.getGoal().equals(other.getGoal()))
            return false;
        return true;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.SolveGoalSuper;
    }
}
