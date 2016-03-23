package dtu.agency.planners.actions;

import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Direction;
import dtu.agency.board.Level;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public abstract boolean checkPreconditions(Level level, HTNEffect effect);

    // Any High Level Action can be refined, as per the Hierarchical Task Network (HTN) approach
    //public abstract List<List<AbstractAction>> getRefinements();
    public abstract ArrayList<LinkedList<AbstractAction>> getRefinements(Direction dirToBox);

}
