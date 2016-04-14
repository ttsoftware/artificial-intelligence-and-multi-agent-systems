package dtu.agency.actions.abstractaction.actioncomparators;

import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.board.Position;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;

/**
 * Comparator to compare SolveGoalActions
 */
public class SolveGoalActionComparator implements Comparator<SolveGoalAction>{

    private final Position agentOrigin;

    public SolveGoalActionComparator(Position agentOrigin) {
        this.agentOrigin = agentOrigin;
    }

    @Override
    public int compare(SolveGoalAction o1, SolveGoalAction o2) {
        return o2.approximateSteps(agentOrigin) - o1.approximateSteps(agentOrigin);
    }
}
