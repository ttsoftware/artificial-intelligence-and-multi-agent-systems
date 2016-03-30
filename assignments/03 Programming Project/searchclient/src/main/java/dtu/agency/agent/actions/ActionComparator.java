package dtu.agency.agent.actions;

import java.util.Comparator;

public class ActionComparator implements Comparator<Action> {

    @Override
    public int compare(Action actionA, Action actionB) {
        return actionA.getHeuristic() - actionB.getHeuristic();
    }
}
