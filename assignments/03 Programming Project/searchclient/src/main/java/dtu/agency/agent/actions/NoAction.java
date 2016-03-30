package dtu.agency.agent.actions;

import dtu.agency.agent.actions.effects.Effect;
import dtu.agency.agent.actions.preconditions.Precondition;

import java.util.List;

public class NoAction extends Action {

    public NoAction() {
        // I do nothing at all
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

    @Override
    public List<Precondition> getPreconditions() {
        return null;
    }

    @Override
    public List<Effect> getEffects() {
        return null;
    }

    @Override
    public String toString() {
        return "NoOp";
    }

    @Override
    public int getHeuristic() {
        return 0;
    }

    @Override
    public void setHeuristic(int heuristic) {

    }
}
