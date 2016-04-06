package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.GotoAbstractAction;
import dtu.agency.actions.abstractaction.MoveBoxAbstractAction;

import java.util.Stack;

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
                plan = new GotoPOP(agent).plan((GotoAbstractAction) action);
                break;
            case MoveBoxAction:
                plan = new MoveBoxPOP(agent).plan((MoveBoxAbstractAction) action);
                break;
        }

        return plan;
    }

    public POPPlan plan1() {
        Stack<ConcreteAction> concreteActions = new Stack<>();
        concreteActions.add(new MoveConcreteAction(Direction.EAST));
        concreteActions.add(new MoveConcreteAction(Direction.EAST));
        concreteActions.add(new MoveConcreteAction(Direction.EAST));
        concreteActions.add(new MoveConcreteAction(Direction.EAST));
        return new POPPlan(concreteActions);
    }
}