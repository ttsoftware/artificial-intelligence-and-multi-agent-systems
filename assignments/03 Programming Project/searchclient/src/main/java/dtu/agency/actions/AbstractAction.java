package dtu.agency.actions;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.services.DebugService;

public abstract class AbstractAction implements Action<AbstractActionType> {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

}
