package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;

import java.util.Comparator;

public class ActionComparator implements Comparator<ConcreteAction> {

    @Override
    public int compare(ConcreteAction concreteActionA, ConcreteAction concreteActionB) {
        return concreteActionA.getHeuristicValue() - concreteActionB.getHeuristicValue();
    }
}
