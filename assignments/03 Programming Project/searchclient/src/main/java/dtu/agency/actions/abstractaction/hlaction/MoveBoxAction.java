package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.planners.htn.HTNState;

import java.util.ArrayList;

public class MoveBoxAction extends HLAction {

    private final Box box;
    private final Position moveToPosition;

    public MoveBoxAction(Box box, Position moveToPosition) {
        this.box = box;
        this.moveToPosition = moveToPosition;
    }

    public Box getBox() {
        return box;
    }

    @Override
    public Position getDestination() {
        return moveToPosition;
    }

    @Override
    public boolean isPureHLAction() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("MoveBoxAction(");
        s.append(getBox().toString());
        s.append(",");
        s.append(moveToPosition.toString());
        s.append(")");
        return s.toString();
    }

    /**
     * TODO: What is this even?
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MoveBoxAction other = (MoveBoxAction) obj;
        if (!this.getBox().equals(other.getBox()))
            return false;
        if (!this.moveToPosition.equals(other.moveToPosition))
            return false;
        if (!this.getDestination().equals(other.getDestination()))
            return false;
        return true;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.MoveBoxAction;
    }
}
