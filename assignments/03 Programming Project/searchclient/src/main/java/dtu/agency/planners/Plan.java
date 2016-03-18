package dtu.agency.planners;

import dtu.agency.planners.actions.AbstractAction;

import java.util.List;

public interface Plan {
    List<AbstractAction> getActions();
}
