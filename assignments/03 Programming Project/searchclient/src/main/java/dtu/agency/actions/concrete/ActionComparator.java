package dtu.agency.actions.concrete;

import java.util.Comparator;

public class ActionComparator implements Comparator<Action> {

    @Override
    public int compare(Action actionA, Action actionB) {
        return actionA.getHeuristicValue() - actionB.getHeuristicValue();
    }
}
