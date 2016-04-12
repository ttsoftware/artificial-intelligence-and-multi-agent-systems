package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

import java.io.Serializable;

/**
* This Action Does Nothing..
*/
public class NoAction extends HLAction implements Serializable {

    private final Position agentDestination;

    public NoAction(Position target) throws AssertionError {
        this.agentDestination = target;
        if (this.agentDestination == null) {
            throw new AssertionError("NoAction: null values not accepted for box or agentDestination");
        }
    }

    public NoAction(NoAction other) {
        this.agentDestination = new Position(other.getDestination());
    }

    @Override
    public Position getDestination() {
        return agentDestination;
    }

    @Override
    public Box getBox() { return null; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.No;
    }

    @Override
    public String toString() {
        return "NoAction()";
    }

}