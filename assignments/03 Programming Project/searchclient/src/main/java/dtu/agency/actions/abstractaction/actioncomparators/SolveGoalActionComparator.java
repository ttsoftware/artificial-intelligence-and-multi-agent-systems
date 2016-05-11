package dtu.agency.actions.abstractaction.actioncomparators;

import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

import java.util.Comparator;

/**
 * Comparator to compare SolveGoalActions
 */
public class SolveGoalActionComparator implements Comparator<SolveGoalAction>{

    private final PlanningLevelService pls;

    public SolveGoalActionComparator(PlanningLevelService pls) {
        this.pls = pls;
    }

    @Override
    public int compare(SolveGoalAction o1, SolveGoalAction o2) {
        return o1.approximateSteps(pls)- o2.approximateSteps(pls);
    }
}
