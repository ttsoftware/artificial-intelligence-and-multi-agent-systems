package dtu.agency.planners;

import dtu.agency.agent.actions.Action;

import java.util.List;

public interface ConcretePlan extends Plan<Action> {
    List<Action> getActions();
    String toString();
}
