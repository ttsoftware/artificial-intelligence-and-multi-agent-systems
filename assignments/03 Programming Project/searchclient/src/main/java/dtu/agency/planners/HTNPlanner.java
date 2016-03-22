package dtu.agency.planners;

import dtu.agency.board.Goal;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.GotoAction;

import java.util.ArrayList;
import java.util.List;

public class HTNPlanner {

    public HTNPlanner(Goal goal) {
        
    }

    public HTNPlan plan() {
        List<AbstractAction> actions = new ArrayList<>();
        actions.add(new GotoAction(0, 5));
        return new HTNPlan(actions);
    }
}
