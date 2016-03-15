package dtu.planners;

import dtu.planners.firstorder.actions.Action;

import java.util.List;

public interface Plan {
    List<Action> getActions();
}
