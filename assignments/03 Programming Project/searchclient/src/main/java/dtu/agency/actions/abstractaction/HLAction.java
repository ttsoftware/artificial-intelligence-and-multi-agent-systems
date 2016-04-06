package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class HLAction extends AbstractAction implements Serializable {

    /*
     * This serves as a heuristic to calculate distances for the heuristics
     */
    public abstract Position getDestination();

    /*
     * TODO: This returns false almost always - maybe we can revise this?
     * Tells if this HLAction refines purely to OTHER HLActions (primitive actions are allowed), for heuristic purposes
     */
    public abstract boolean isPureHLAction();

    /*
     * Checks whether the purpose/sub goal of this high level action is completed
     */
    public abstract boolean isPurposeFulfilled(HTNState htnState);

    /*
     * Creates an empty refinement of known signature, to return if sub goal is completed
     */
    public ArrayList<MixedPlan> doneRefinement() {
        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement = new MixedPlan();
        refinement.addAction(new NoConcreteAction());
        refinements.add(refinement);
        return refinements;
    }

    /*
     * Any High Level ConcreteAction can be refined, as per the Hierarchical Task Network (HTN) approach
     */
    public abstract ArrayList<MixedPlan> getRefinements(HTNState priorState);

    @Override
    public abstract String toString();
}
