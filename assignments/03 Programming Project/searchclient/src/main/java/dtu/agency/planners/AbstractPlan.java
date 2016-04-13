package dtu.agency.planners;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Position;

import java.util.List;

public interface AbstractPlan extends Plan<AbstractAction> {
    List<? extends AbstractAction> getActions();
    public int approximateSteps(Position agentOrigin);

}
