package dtu.agency.planners;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.planners.actions.AbstractAction;

import java.util.ArrayList;
import java.util.List;

public class PartialOrderPlanner {

    public PartialOrderPlanner(AbstractAction action) {

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