package dtu.agency.agent.actions;

import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.io.Serializable;

public abstract class Action extends AbstractAction implements Serializable {

    public abstract ActionType getType();

    @Override
    public abstract String toString();

    public abstract HTNEffect applyTo(HTNEffect oldState);
}
