package dtu.agency.planners.actions;

import dtu.agency.agent.actions.*;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.effects.HTNEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MoveBoxAction extends HLAction {

    private final Position finalDestination;
    private final Box targetBox;
    private final Goal targetGoal;

    public MoveBoxAction(Box box, Position target) {
        this.finalDestination = target;
        this.targetBox = box;
        this.targetGoal = null;
    }

    public MoveBoxAction(Box box, Goal goal) {
        this.finalDestination = goal.getPosition();
        this.targetBox = box;
        this.targetGoal = goal;
    }

    public Box getBox() {
        return targetBox;
    }

    public Position getDestination() {
        return finalDestination;
    }

    public Goal getGoal() {
        return targetGoal;
    }

    @Override
    public boolean checkPreconditions( HTNEffect effect) {
        System.err.print("Check Preconditions Not Implemented!");
        return false;
    }

    @Override
    public boolean isPurposeFulfilled(HTNEffect effect) {
        if (effect.getBoxPosition().equals(this.finalDestination)) { //
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNEffect priorState) {
        System.err.println("MoveBoxAction.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        if (!priorState.boxIsMovable()) {
            System.err.println("Box not movable, empty refinements returned.");
            return refinements;
        }

        Direction dirToBox = priorState.getDirectionToBox();
        System.err.println("direction to box: " + dirToBox.toString());

        // PUSH REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action push = new PushAction(targetBox, dirToBox, dir);
            HTNEffect result = push.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !

            // print the action along with its result (only after validated)
            //System.err.println("Action:" + push.toString() + ", " + result.toString());

            refinement.addAction(push);
            refinement.addAction(this);

            //System.err.println(refinement.toString());

            refinements.add(refinement);
        }

        // PULL REFINEMENTS
        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action pull = new PullAction(targetBox, dir, dirToBox);
            HTNEffect result = pull.applyTo(priorState);
            if (result == null) continue; // then the action was illegal !

            // print the action along with its result (only after validated)
            //System.err.println("Action:" + pull.toString() + ", Result:" + result.toString());

            // check if any of the resulting states fulfills this HLActions target,
            // and if so, return only the action which does!
            refinement.addAction(pull);
            refinement.addAction(this);

            refinements.add(refinement);
        }

        // else shuffle (NO done in HTNNODE) and return all refinements
        // long seed = System.nanoTime();
        // Collections.shuffle(refinements, new Random(seed));
        System.err.println(refinements.toString());

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
