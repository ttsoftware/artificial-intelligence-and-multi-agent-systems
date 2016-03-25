package dtu.agency.planners;


import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Action;

import java.util.List;

public interface AbstractPlan extends Plan<AbstractAction> {
    List<? extends AbstractAction> getActions();
}
