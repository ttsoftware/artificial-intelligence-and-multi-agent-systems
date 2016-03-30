package dtu.agency.planners.pop;

import dtu.agency.board.Agent;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;

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
            case GotoAction:
                plan = new GotoPOP(agent).plan((GotoAction) action);
                break;
            case MoveBoxAction:
                plan = new MoveBoxPOP(agent).plan((MoveBoxAction) action);
                break;
        }

        return plan;
    }
}