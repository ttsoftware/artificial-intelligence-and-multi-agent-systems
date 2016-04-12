package dtu.agency.actions;

import dtu.agency.actions.concreteaction.ConcreteActionType;
import dtu.agency.services.DebugService;

public abstract class ConcreteAction implements Action<ConcreteActionType> {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

    protected int heuristicValue;

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }
}