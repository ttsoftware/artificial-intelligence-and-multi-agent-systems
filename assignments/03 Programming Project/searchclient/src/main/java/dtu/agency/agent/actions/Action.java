package dtu.agency.agent.actions;

import dtu.agency.AbstractAction;

public abstract class Action extends AbstractAction {

    public abstract ActionType getType();

    @Override
    public abstract String toString();
}
