package dtu.agency.planners.htn;

import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;

import java.util.LinkedList;
import java.util.List;

public class HTNPlan {

    private RGotoAction RGotoAction;
    private RMoveBoxAction RMoveBoxAction;

    public HTNPlan(RGotoAction gta, RMoveBoxAction mba) {
        this.RGotoAction = gta;
        this.RMoveBoxAction = mba;
    }

    public HTNPlan(MixedPlan mpl) {
        this.RGotoAction = (RGotoAction) mpl.removeFirst();
        this.RMoveBoxAction = (RMoveBoxAction) mpl.removeFirst();
    }

    public List<HLAction> getActions() {
        LinkedList<HLAction> actions = new LinkedList<>();
        if (this.RGotoAction !=null) {actions.add(this.RGotoAction);}
        if (this.RMoveBoxAction !=null) {actions.add(this.RMoveBoxAction);}
        return actions;
    }


    public RGotoAction getRGotoAction() {
        return RGotoAction;
    }

    public RMoveBoxAction getRMoveBoxAction() {
        return RMoveBoxAction;
    }

    public void setRGotoAction(RGotoAction RGotoAction) {
        this.RGotoAction = RGotoAction;
    }

    public void setRMoveBoxAction(RMoveBoxAction RMoveBoxAction) {
        this.RMoveBoxAction = RMoveBoxAction;
    }

    public void clearActions() {
        this.RGotoAction = null;
        this.RMoveBoxAction = null;
    }

    public boolean isEmpty() { return (this.RGotoAction ==null && this.RMoveBoxAction ==null); }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("HTNPlan[");
        if (getRGotoAction()!=null) {
            s.append(getRGotoAction().toString());
        } else {
            s.append("null");
        }
        s.append(",");
        if (getRMoveBoxAction()!=null) {
            s.append(getRMoveBoxAction().toString());
        } else {
            s.append("null");
        }
        s.append("]");
        return s.toString();
    }
}