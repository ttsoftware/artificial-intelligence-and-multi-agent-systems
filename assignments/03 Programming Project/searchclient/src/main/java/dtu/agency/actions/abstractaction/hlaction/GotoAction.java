package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;
import java.util.ArrayList;

public class GotoAction extends HLAction implements Serializable {

    private final Position gotoPosition;

    public GotoAction(Position gotoPosition) {
        this.gotoPosition = gotoPosition;
    }

    @Override
    public Position getDestination() {
        return gotoPosition;
    }

    @Override
    public boolean isPureHLAction() {
        return false;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.GotoAction;
    }

    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        boolean fulfilled = false;
        if (htnState.getAgentPosition().isAdjacentTo(getDestination())) { //
            fulfilled = true;
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
            ConcreteAction move = new MoveConcreteAction(dir);
            HTNState result = priorState.applyConcreteAction(move);
            if (result == null) continue; // illegal move, discard it
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
        if (getDestination() != null) {
            s.append(getDestination().toString());
        } else {
            s.append(getDestination().toString());
        }
        s.append(")");
        return s.toString();
    }

    /**
     * TODO: What do we need all these equals for?
     *
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
        GotoAction other = (GotoAction) obj;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }
}
