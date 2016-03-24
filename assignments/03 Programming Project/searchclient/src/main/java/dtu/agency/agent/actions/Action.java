package dtu.agency.agent.actions;

import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.planners.actions.preconditions.Precondition;

import java.io.Serializable;
import java.util.List;

public abstract class Action implements Serializable {

    public abstract ActionType getType();

    public abstract List<Precondition> getPreconditions();

    public abstract List<Effect> getEffects();

    @Override
    public abstract String toString();
}
