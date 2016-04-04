package dtu.agency.planners.actions;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;
import java.io.Serializable;
import java.util.ArrayList;

public class GotoAction extends HLAction implements Serializable {

    private final Position agentDestination;
    private final Box targetBox;

    public GotoAction(Position position) {
        this.agentDestination = position;
        this.targetBox = null;
        if (agentDestination == null) {
            throw new AssertionError("GotoAction: null values not accepted as target destination");
        }
    }

    public GotoAction(Box box) {
        this.agentDestination = box.getPosition();
        this.targetBox = box;
        if (agentDestination == null) {
            throw new AssertionError("GotoAction: null values not accepted as target destination");
        }
    }

    public Box getTargetBox() {
        return targetBox;
    }

    @Override
    public Position getDestination() {
        return this.agentDestination;
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
        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("GotoAction(");
        if (getTargetBox() != null) {
            s.append(getTargetBox().toString());
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
        GotoAction other = (GotoAction) obj;
        if (!this.getTargetBox().equals(other.getTargetBox()))
            return false;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }

}
