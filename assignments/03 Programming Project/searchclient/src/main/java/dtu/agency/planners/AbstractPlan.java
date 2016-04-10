package dtu.agency.planners;

import dtu.agency.actions.AbstractAction;
import java.util.List;

public interface AbstractPlan extends Plan<AbstractAction> {
    List<? extends AbstractAction> getActions();
}
