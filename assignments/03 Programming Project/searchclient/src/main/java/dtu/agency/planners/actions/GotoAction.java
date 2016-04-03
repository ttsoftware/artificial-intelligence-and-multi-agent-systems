package dtu.agency.planners.actions;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;
import java.util.*;

public class GotoAction extends HLAction implements Serializable {

    private final Position agentDestination;
    private final Box targetBox;

    public GotoAction(Position position) {
        this.agentDestination = position;
        this.targetBox = null;
    }

    public GotoAction(Box box) {
        this.agentDestination = box.getPosition();
        this.targetBox = box;
    }

    @Override
    public Position getDestination() {
        return this.agentDestination;
    }

    public Box getTargetBox() {
        return targetBox;
    }

    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        boolean fulfilled = false;
        if (this.targetBox == null) { // no box, agent should end up at agentDestination
            if (htnState.getAgentPosition().equals(this.agentDestination)) { //
                fulfilled = true;
            }
        } else { // box target, agent should end up at neighbouring cell
            if (htnState.getAgentPosition().isNeighbour(this.agentDestination)) { //
                fulfilled = true;
            }
        }
        //if (fulfilled) System.err.println("This HLAction " + this.toString() + " is fulfilled" );
        return fulfilled;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
        // check if the prior state fulfills this HLActions target, and if so return empty plan of refinements
        // System.err.println("GotoAction.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            MixedPlan refinement = new MixedPlan();
            Action move = new MoveAction(dir);
            HTNState result = move.applyTo(priorState);
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
