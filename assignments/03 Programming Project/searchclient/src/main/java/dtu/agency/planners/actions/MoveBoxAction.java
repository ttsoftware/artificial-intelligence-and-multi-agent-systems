package dtu.agency.planners.actions;

import dtu.agency.agent.actions.*;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.util.ArrayList;

public class MoveBoxAction extends HLAction {

    private final Position boxDestination;
    private final Box targetBox;
    private final Goal targetGoal;

    public MoveBoxAction(Box box, Position target) {
        this.boxDestination = target;
        this.targetBox = box;
        this.targetGoal = null;
        if (box == null || target == null) {
            throw new AssertionError("MoveBoxAction: null values not accepted as target box or destination");
        }
    }

    public MoveBoxAction(Box box, Goal goal) {
        this.boxDestination = goal.getPosition();
        this.targetBox = box;
        this.targetGoal = goal;
        if (box == null || goal == null || boxDestination == null) {
            throw new AssertionError("MoveBoxAction: null values not accepted as target box or destination");
        }
    }

    public Box getBox() {
        return targetBox;
    }

    public Goal getGoal() {
        return targetGoal;
    }

    @Override
    public Position getDestination() {
        return boxDestination;
    }

    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        if (htnState.getBoxPosition().equals(this.boxDestination)) { //
            //System.err.println("This HLAction " + this.toString() + " is fulfilled" );
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
        //System.err.println("MoveBoxAction.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();
        ArrayList<MixedPlan> refinements = new ArrayList<>();

        if (!priorState.boxIsMovable()) {
            //System.err.println("Box not movable, empty refinements returned.");
            return refinements;
        }

        Direction dirToBox = priorState.getDirectionToBox();
        //System.err.println("direction to box: " + dirToBox.toString());

        // PUSH REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action push = new PushAction(targetBox, dirToBox, dir);
            HTNState result = push.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !
            refinement.addAction(push);
            refinement.addAction(this);
            refinements.add(refinement);
            //System.err.println("Action:" + push.toString() + ", " + result.toString());
            //System.err.println(refinement.toString());
        }

        // PULL REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action pull = new PullAction(targetBox, dir, dirToBox);
            HTNState result = pull.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !
            refinement.addAction(pull);
            refinement.addAction(this);
            refinements.add(refinement);
            //System.err.println("Action:" + pull.toString() + ", Result:" + result.toString());
        }
        //System.err.println(refinements.toString());
        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("MoveBoxAction(");
        s.append(getBox().toString());
        s.append(",");
        if (getGoal() != null) {
            s.append(getGoal().toString());
        } else {
            s.append(getDestination().toString());
        }
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
        MoveBoxAction other = (MoveBoxAction) obj;
        if (!this.getBox().equals(other.getBox()))
            return false;
        if (!this.getGoal().equals(other.getGoal()))
            return false;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }

}
