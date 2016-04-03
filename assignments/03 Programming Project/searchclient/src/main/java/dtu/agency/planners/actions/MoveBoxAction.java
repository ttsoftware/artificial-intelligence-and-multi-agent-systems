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
    }

    public MoveBoxAction(Box box, Goal goal) {
        this.boxDestination = goal.getPosition();
        this.targetBox = box;
        this.targetGoal = goal;
    }

    public Box getBox() {
        return targetBox;
    }

    @Override
    public Position getDestination() {
        return boxDestination;
    }

    public Goal getGoal() {
        return targetGoal;
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

        // shuffling is done in HTNNode
        //System.err.println(refinements.toString());
        return refinements;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("MoveBoxAction(");
        if (getBox()!=null) {
            s.append(getBox().toString());
        } else {
            s.append("null");
        }
        s.append(",");
        if (getGoal() != null) {
            s.append(getGoal().toString());
        } else {
            if (getDestination() != null) {
                s.append(getDestination().toString());
            } else {
                s.append("null");
            }
        }
        s.append(")");
        return s.toString();
    }

}
