package dtu.agency.actions.concrete;

import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.htn.HTNState;

import java.io.Serializable;

public abstract class Action extends AbstractAction implements Serializable {

    protected int heuristicValue;

    // public abstract ActionType getType();

    /*
    * Used by HTN planner to get information of the new state, if Action is performed.
    * ** heuristic of relaxation could be added here **
    */
    public abstract HTNState applyTo(HTNState oldState);

    @Override
    public abstract String toString();

    public int getHeuristicValue() {
        return heuristicValue;
    }
}