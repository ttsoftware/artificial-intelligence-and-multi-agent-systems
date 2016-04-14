package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

import java.io.Serializable;

/**
* This Action tries to circumvent a Box in an open environment using just concrete 'move' actions
*/
public class CircumventBoxAction extends HLAction implements Serializable {

    private final Box box;
    private final Position agentDestination;

    public CircumventBoxAction(Box box, Position target) throws AssertionError {
        this.box = box;
        this.agentDestination = target;
        if (this.box == null || this.agentDestination == null) {
            throw new AssertionError("CircumventBoxAction: null values not accepted for box or agentDestination");
        }
    }

    public CircumventBoxAction(CircumventBoxAction other) {
        this.box = new Box(other.getBox());
        this.agentDestination = new Position(other.getAgentDestination());
    }

    public Position getAgentDestination() { return new Position(agentDestination); }

    @Override
    public Position getBoxDestination() {
        // TODO
        return null;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.Circumvent;
    }

    @Override
    public Box getBox() {
        return new Box(box);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("CircumventBoxAction(");
        s.append(getBox().toString());
        s.append(",");
        s.append(getAgentDestination().toString());
        s.append(")");
        return s.toString();
    }

    @Override
    public int approximateSteps(Position agentOrigin) {
        return agentOrigin.manhattanDist(agentDestination);
    }

}
