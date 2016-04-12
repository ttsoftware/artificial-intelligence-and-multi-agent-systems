package dtu.agency.planners.pop;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.GotoAbstractAction;
import dtu.agency.actions.abstractaction.MoveBoxAbstractAction;
import dtu.agency.board.Agent;

public class PartialOrderPlanner {

    private AbstractAction action;
    private Agent agent;

    public PartialOrderPlanner(AbstractAction action, Agent agent) {
        this.action = action;
        this.agent = agent;
    }

    public POPPlan plan() {
        POPPlan plan = null;

        switch (action.getType()) {
            case RGotoAction:
                plan = new GotoPOP(agent).plan((GotoAbstractAction) action);
                break;
            case MoveBoxAction:
                plan = new MoveBoxPOP(agent).plan((MoveBoxAbstractAction) action);
                break;
        }

        return plan;
    }
}