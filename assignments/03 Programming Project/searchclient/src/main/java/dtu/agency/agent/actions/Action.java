package dtu.agency.agent.actions;

import java.io.Serializable;

public abstract class Action implements Serializable {

    public abstract ActionType getType();

    @Override
    public abstract String toString();
}
