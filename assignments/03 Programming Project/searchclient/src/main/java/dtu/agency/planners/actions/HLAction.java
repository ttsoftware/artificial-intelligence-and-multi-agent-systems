package dtu.agency.planners.actions;

import dtu.agency.agent.actions.NoAction;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class HLAction extends AbstractAction implements Serializable {

    // mads: should not be a collection, but rather the single accumulated state in the given parameters
    // e.g. HTNState has agent and box position, nothing else changes from the perspective of this action,
    // as it is residing within a planner, tasked with a single agent and a single box (it is NOT global)
    protected HTNState state;

    public HTNState getState() {
        return state;
    }

    public void setEffects(HTNState htnState) {
        this.state = htnState;
    }


    /*
     * This serves as a method to calculate distances for the heuristics
     */
    public abstract Position getDestination();

    /*
     * Tells if this HLAction refines purely to other HLActions, for heuristic purposes
     */
    public boolean isPureHLAction() { return false; }

    /*
     * Checks whether the purpose/subgoal of this highlevel action is completed
     */
    public abstract boolean isPurposeFulfilled( HTNState htnState );

    /*
     * Creates an empty refinement of known signature, to return if subgoal is completed
     */
    public ArrayList<MixedPlan> doneRefinement() {
        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement = new MixedPlan();
        refinement.addAction( new NoAction() );
        refinements.add(refinement);
        return refinements;
    }

    /*
     * Any High Level Action can be refined, as per the Hierarchical Task Network (HTN) approach
     */
    public abstract ArrayList<MixedPlan> getRefinements(HTNState priorState);

    @Override
    public abstract String toString();
}
