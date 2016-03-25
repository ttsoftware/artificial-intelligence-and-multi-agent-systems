package dtu.agency.agent.actions;

import dtu.agency.AbstractAction;
import dtu.agency.planners.actions.effects.HTNEffect;

public abstract class Action extends AbstractAction {

    public abstract ActionType getType();

    @Override
    public abstract String toString();

    public abstract HTNEffect applyTo(HTNEffect oldState);
}
