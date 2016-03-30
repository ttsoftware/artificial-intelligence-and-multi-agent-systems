package dtu.agency.agent.actions;

import java.util.Comparator;

public class ActionComparator implements Comparator<Action> {

    @Override
    public int compare(Action o1, Action o2) {
        return o1.getHeuristic() - o2.getHeuristic();
    }
}
