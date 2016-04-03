package dtu.agency.planners.actions;

import dtu.agency.agent.actions.NoAction;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class HLAction extends AbstractAction implements Serializable {

    // mads: should not be a collection, but rather the single accumulated effect in the given parameters
    // e.g. HTNEffect has agent and box position, nothing else changes from the perspective of this action,
    // as it is residing within a planner, tasked with a single agent and a single box (it is NOT global)
    protected HTNEffect effect;

    public HTNEffect getEffect() {
        return effect;
    }

    public void setEffects(HTNEffect effect) {
        this.effect = effect;
    }

    // mads: preconditions could be checked by functions, ensuring the effects and levels
    public abstract boolean checkPreconditions( HTNEffect effect );

    public abstract boolean isPurposeFulfilled( HTNEffect effect );

    protected ArrayList<MixedPlan> doneRefinement() {
        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement = new MixedPlan();
        refinement.addAction( new NoAction() );
        refinements.add(refinement);
        return refinements;
    }


    // Any High Level Action can be refined, as per the Hierarchical Task Network (HTN) approach
    //public abstract List<List<AbstractAction>> getRefinements();
    public abstract ArrayList<MixedPlan> getRefinements(HTNEffect priorState);

    @Override
    public abstract String toString();
}
