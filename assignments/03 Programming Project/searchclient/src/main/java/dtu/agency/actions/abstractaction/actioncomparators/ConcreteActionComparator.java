package dtu.agency.actions.abstractaction.actioncomparators;

import dtu.agency.actions.ConcreteAction;

import java.util.Comparator;

public class ConcreteActionComparator implements Comparator<ConcreteAction> {

    @Override
    public int compare(ConcreteAction concreteActionA, ConcreteAction concreteActionB) {
        return concreteActionA.getHeuristicValue() - concreteActionB.getHeuristicValue();
    }
}
