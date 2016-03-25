package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.List;

public interface Plan<T> {
    List<? extends T> getActions();
}
