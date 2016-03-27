package dtu.agency.planners.actions;

import dtu.agency.agent.actions.*;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.util.*;

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
    public boolean checkPreconditions(Level level, HTNEffect effect) {
        return false;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNEffect priorState, Level level) {

        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement;
        Action push, pull;
        HTNEffect result;

        if (!priorState.boxIsMovable()) { return refinements; }
        Direction dirToBox = priorState.getDirectionToBox();
        // then can we check if push/pull  direction os valid before adding it??
        // else leave it for someone else

        // PUSH REFINEMENTS
        for (Direction dir : Direction.values()) {
            refinement = new MixedPlan();

            push = new PushAction(targetBox, dirToBox, dir);
            result = push.applyTo(priorState);
            if (!result.isLegal(level)) continue;

            // check if any of the resulting states fulfills this HLActions target,
            // and if so, return only the action which does!
            refinement.addAction(push);
            if (!result.getBoxPosition().equals(this.finalDestination)) { //
                refinement.addAction(this);
            } // if not, add this abstract action again

            refinements.add(refinement);
        }

        // PULL REFINEMENTS
        for (Direction dir : Direction.values()) {
            refinement = new MixedPlan();

            pull = new PullAction(targetBox, dir, dirToBox);
            result = pull.applyTo(priorState);
            if (!result.isLegal(level)) continue;

            // check if any of the resulting states fulfills this HLActions target,
            // and if so, return only the action which does!
            refinement.addAction(pull);
            if (!result.getBoxPosition().equals(this.finalDestination)) { //
                refinement.addAction(this);
            } // if not, add this abstract action again

            refinements.add(refinement);
        }

        // else shuffle and return all refinements
        long seed = System.nanoTime();
        Collections.shuffle(refinements, new Random(seed));

        return refinements;
    }

}
