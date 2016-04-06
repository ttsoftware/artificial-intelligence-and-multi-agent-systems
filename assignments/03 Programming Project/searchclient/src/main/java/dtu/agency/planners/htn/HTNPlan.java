package dtu.agency.planners.htn;

import dtu.agency.actions.abstractaction.hlaction.GotoAction;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.MoveBoxAction;

import java.util.LinkedList;
import java.util.List;

public class HTNPlan {

    private GotoAction gotoAction;
    private MoveBoxAction moveBoxAction;

    public HTNPlan(GotoAction gta, MoveBoxAction mba) {
        this.gotoAction = gta;
        this.moveBoxAction = mba;
    }

    public HTNPlan(MixedPlan mpl) {
        this.gotoAction = (GotoAction) mpl.removeFirst();
        this.moveBoxAction = (MoveBoxAction) mpl.removeFirst();
    }

    public List<HLAction> getActions() {
        LinkedList<HLAction> actions = new LinkedList<>();
        if (this.gotoAction!=null) {actions.add(this.gotoAction);}
        if (this.moveBoxAction!=null) {actions.add(this.moveBoxAction);}
        return actions;
    }


    public GotoAction getGotoAction() {
        return gotoAction;
    }

    public MoveBoxAction getMoveBoxAction() {
        return moveBoxAction;
    }

    public void setGotoAction(GotoAction gotoAction) {
        this.gotoAction = gotoAction;
    }

    public void setMoveBoxAction(MoveBoxAction moveBoxAction) {
        this.moveBoxAction = moveBoxAction;
    }

    public void clearActions() {
        this.gotoAction = null;
        this.moveBoxAction = null;
    }

    public boolean isEmpty() { return (this.gotoAction==null && this.moveBoxAction==null); }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("HTNPlan[");
        if (getGotoAction()!=null) {
            s.append(getGotoAction().toString());
        } else {
            s.append("null");
        }
        s.append(",");
        if (getMoveBoxAction()!=null) {
            s.append(getMoveBoxAction().toString());
        } else {
            s.append("null");
        }
        s.append("]");
        return s.toString();
    }
}