package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

import java.io.Serializable;

/*
* This Action tries to circumvent a Box in an open environment using just concrete 'move' actions
*/
public class NoAction extends HLAction implements Serializable {

    private final Position agentDestination;

    public NoAction(Position target) throws AssertionError {
        this.agentDestination = target;
        if (this.agentDestination == null) {
            throw new AssertionError("NoAction: null values not accepted for box or agentDestination");
        }
    }

    @Override
    public Position getDestination() {
        return agentDestination;
    }

    @Override
    public Box getBox() { return null; }

    @Override
    public boolean isPureHLAction() { return false; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.No;
    }

    @Override
    public String toString() {
        return "NoAction()";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoAction other = (NoAction) obj;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }
}
