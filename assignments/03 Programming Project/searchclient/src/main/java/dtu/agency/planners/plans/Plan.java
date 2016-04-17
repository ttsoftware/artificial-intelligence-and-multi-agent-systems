package dtu.agency.planners.plans;

import dtu.agency.actions.Action;

import java.util.List;

public interface Plan<T extends Action> {
    List<? extends T> getActions(); // Needs to be an ORDERED list, the actions can NOT be done in a random sequence
}
