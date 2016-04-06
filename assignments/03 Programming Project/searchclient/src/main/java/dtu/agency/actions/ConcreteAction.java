package dtu.agency.actions;

import dtu.agency.actions.concreteaction.ConcreteActionType;
import dtu.agency.planners.htn.HTNState;

public abstract class ConcreteAction implements Action<ConcreteActionType> {

    protected int heuristicValue;

    /*
    * Used by HTN planner to get information of the new state, if ConcreteAction is performed.
    * ** heuristic of relaxation could be added here **
    */
    public abstract HTNState applyTo(HTNState oldState);

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }
}