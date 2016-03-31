package dtu.agency.agent.actions;

import java.io.Serializable;

public abstract class Action implements Serializable {

    protected int heuristicValue;

    public abstract ActionType getType();

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }
}