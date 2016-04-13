package dtu.agency.actions.abstractaction.rlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class RGotoAction extends RLAction {

    private final Position agentDestination;

    public RGotoAction(Position agentDestination) {
        this.agentDestination = agentDestination;
    }

    public RGotoAction(RGotoAction other) {
        agentDestination = new Position(other.getAgentDestination());
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.RGotoAction;
    }

    @Override
    public Position getAgentDestination() {
        return agentDestination;
    }

    @Override
    public Box getBox() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("RGotoAction(");
        if (getAgentDestination() != null) {
            s.append(getAgentDestination().toString());
        } else {
            s.append(getAgentDestination().toString());
        }
        s.append(")");
        return s.toString();
    }

    @Override
    public int approximateSteps(Position agentOrigin) {
        return agentOrigin.manhattanDist(agentDestination);
    }

 }
