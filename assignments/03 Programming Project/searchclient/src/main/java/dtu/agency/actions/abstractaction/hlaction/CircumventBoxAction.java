package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.services.GlobalLevelService;

import java.io.Serializable;
import java.util.ArrayList;

/*
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

    public Box getBox() {
        return box;
    }

    public Position getAgentDestination() { return agentDestination; }

    @Override
    public Position getDestination() {
        return getAgentDestination();
    }

    @Override
    public boolean isPureHLAction() { return false; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.Circumvent;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CircumventBoxAction other = (CircumventBoxAction) obj;
        if (!this.getBox().equals(other.getBox()))
            return false;
        if (!this.getAgentDestination().equals(other.getAgentDestination()))
            return false;
        return true;
    }
}
