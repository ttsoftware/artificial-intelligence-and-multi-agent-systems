package dtu.agency.planners.actions;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.io.Serializable;
import java.util.*;

public class GotoAction extends HLAction implements Serializable {

    private final Position finalDestination;
    private final Box targetBox;

    public GotoAction(int row, int column) {
        this.finalDestination = new Position(row, column);
        this.targetBox = null;
    }

    public GotoAction(Position position) {
        this.finalDestination = position;
        this.targetBox = null;
    }

    public GotoAction(Box box) {
        this.finalDestination = box.getPosition();
        this.targetBox = box;
    }

    public Position getDestination() {
        return this.finalDestination;
    }

    public Box getTargetBox() {
        return targetBox;
    }

    @Override
    public boolean checkPreconditions(HTNEffect effect) {
        System.err.print("Check Preconditions Not Implemented!");
        return false;
    }

    @Override
    public boolean isPurposeFulfilled(HTNEffect effect) {
        boolean fulfilled = false;
        if (this.targetBox == null) { // no box, agent should end up at finalDestination
            if (effect.getAgentPosition().equals(this.finalDestination)) { //
                fulfilled = true;
            }
        } else { // box target, agent should end up at neighbouring cell
            if (effect.getAgentPosition().isNeighbour(this.finalDestination)) { //
                fulfilled = true;
            }
        }
        if (fulfilled) System.err.println("This HLAction " + this.toString() + " is fulfilled" );
        return fulfilled;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNEffect priorState) {
        // check if the prior state fulfills this HLActions target, and if so return empty plan of refinements
        System.err.println("GotoAction.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action move = new MoveAction(dir);
            HTNEffect result = move.applyTo(priorState);
            if (result==null) continue; // illegal move, discard it
            refinement.addAction(move);
            refinement.addAction(this); // append this action again
            refinements.add(refinement);
        }

        // else shuffle (no done in HTNNODE) and return all refinements
        // long seed = System.nanoTime();
        // Collections.shuffle(refinements, new Random(seed));

        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("GotoAction(");
        if (getTargetBox() != null) {
            s.append(getTargetBox().toString());
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

    public boolean equals(GotoAction o) {
        if (this.getTargetBox().equals(o.getTargetBox()))
            if (this.getDestination().equals(o.getDestination())) return true;
        return false;
    }
}
