package dtu.agency.planners.plans;

import dtu.agency.actions.Action;

import java.util.Collection;

public interface Plan<T extends Action> {
    Collection<? extends T> getActions();
}
