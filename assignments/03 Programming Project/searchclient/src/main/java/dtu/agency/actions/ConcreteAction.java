package dtu.agency.actions;

import dtu.agency.actions.concreteaction.ConcreteActionType;

public abstract class ConcreteAction implements Action<ConcreteActionType> {

    protected int heuristicValue;

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }
}