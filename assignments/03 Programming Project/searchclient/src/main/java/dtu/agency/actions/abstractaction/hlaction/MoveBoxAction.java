package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.util.ArrayList;

public class MoveBoxAction extends HLAction {

    private final Box box;
    private final Position moveToPosition;

    public MoveBoxAction(Box box, Position moveToPosition) {
        this.box = box;
        this.moveToPosition = moveToPosition;
    }

    public Box getBox() {
        return box;
    }

    @Override
    public Position getDestination() {
        return moveToPosition;
    }

    @Override
    public boolean isPureHLAction() {
        return false;
    }

    /**
     * TODO: Add comments here
     * @param htnState
     * @return
     */
    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        if (htnState.getBoxPosition().equals(moveToPosition)) { //
            //System.err.println("MBA.isFulfilled " + this.toString() );
            return true;
        }
        return false;
    }

    /**
     * TODO: Add comments here
     * @param priorState
     * @return
     */
    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
//        System.err.println("MBA.getRefinements:" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();
        ArrayList<MixedPlan> refinements = new ArrayList<>();

        if (!priorState.boxIsMovable()) {
            System.err.println("Box not movable, empty refinements returned.");
            return refinements;
        }

        Direction dirToBox = priorState.getDirectionToBox();
//        System.err.println("direction to box: " + dirToBox.toString());

        // PUSH REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            ConcreteAction push = new PushConcreteAction(box, dirToBox, dir);
            HTNState result = push.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !
            refinement.addAction(push);
            refinement.addAction(this);
            refinements.add(refinement);
            //System.err.println("ConcreteAction:" + push.toString() + ", " + result.toString());
            //System.err.println(refinement.toString());
        }

        // PULL REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            ConcreteAction pull = new PullConcreteAction(box, dir, dirToBox);
//            System.err.println(pull.toString());
            HTNState result = pull.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !
            refinement.addAction(pull);
            refinement.addAction(this);
            refinements.add(refinement);
//            System.err.println("ConcreteAction:" + pull.toString() + ", Result:" + result.toString());
        }
//        System.err.println(refinements.toString());
        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("MoveBoxAction(");
        s.append(getBox().toString());
        s.append(",");
        s.append(moveToPosition.toString());
        s.append(")");
        return s.toString();
    }

    /**
     * TODO: What is this even?
     * @param obj
     * @return
     */
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
        if (!this.moveToPosition.equals(other.moveToPosition))
            return false;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.MoveBoxAction;
    }
}
