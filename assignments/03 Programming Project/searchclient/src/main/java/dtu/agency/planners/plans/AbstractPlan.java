package dtu.agency.planners.plans;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

import java.util.List;

public interface AbstractPlan extends Plan<AbstractAction> {
    List<? extends AbstractAction> getActions();
    public int approximateSteps(PlanningLevelService pls);
}
