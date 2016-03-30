package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import dtu.agency.planners.actions.AbstractAction;

import java.util.ArrayList;
import java.util.List;

public class PartialOrderPlanner {

    public PartialOrderPlanner(AbstractAction action, Agent agent, Level level) {
        switch (action.getClass().getName()) {
            case "GoToAction":
                List<Action> actions = goToActionPlanner(action, agent, level);
                break;
            case "MoveBoxAction":

                break;
            default:
                break;
        }
    }

    private List<Action> goToActionPlanner(AbstractAction action, Agent agent, Level level) {
        GotoPOP gotoPOP = new GotoPOP(level, agent);
        List<Action> concreteActions = gotoPOP.search(action);
        return concreteActions;
    }

    public POPPlan plan() {
        List<Action> actions = new ArrayList<>();
        actions.add(new MoveAction(Direction.EAST));
        actions.add(new MoveAction(Direction.EAST));
        actions.add(new MoveAction(Direction.EAST));
        actions.add(new MoveAction(Direction.EAST));
        return new POPPlan(actions);
    }
}