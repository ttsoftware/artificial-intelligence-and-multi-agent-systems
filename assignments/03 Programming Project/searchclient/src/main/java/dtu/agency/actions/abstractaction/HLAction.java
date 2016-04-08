package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.util.ArrayList;

public abstract class HLAction extends AbstractAction {

    @Override
    public abstract AbstractActionType getType();

    /*
     * This is where the agent is going to be after completing this action
     */
    public abstract Position getDestination();

    /*
     * This is the targeted box during this action(s)
     */
    public abstract Box getBox();


    /*
     * TODO: This returns false almost always - maybe we can revise this?
     * TODO: This will change when more types of High Level Actions are written
     * Tells if this HLAction refines purely to OTHER HLActions (primitive actions are allowed), for heuristic purposes
     * If recursive behavior in getRefinements, HLAction is -> UN-pure     else   -> pure
     */
    public abstract boolean isPureHLAction();

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

    @Override
    public abstract String toString();
}
