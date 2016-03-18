package dtu.planners;

import dtu.planners.actions.AbstractAction;

import java.util.List;

public interface Plan {
    List<AbstractAction> getActions();
}
