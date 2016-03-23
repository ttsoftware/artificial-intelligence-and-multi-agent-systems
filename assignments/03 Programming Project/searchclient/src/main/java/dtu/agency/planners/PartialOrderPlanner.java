package dtu.agency.planners;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.planners.actions.AbstractAction;

import java.util.ArrayList;
import java.util.List;

public class PartialOrderPlanner {

    public PartialOrderPlanner(AbstractAction action) {
        switch (action.getClass().getName()) {
            case "GoToAction":
                List<Action> actions = goToActionPlanner(action);
                break;
            case "MoveBoxAction":

                break;
            default:
                break;
        }
    }

    private List<Action> goToActionPlanner(AbstractAction action) {
        List<Action> actions = new ArrayList<Action>();

        

        return actions;
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