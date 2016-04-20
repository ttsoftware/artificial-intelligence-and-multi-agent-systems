package dtu.agency.planners.plans.comparators;

import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.services.PlanningLevelService;

import java.util.Comparator;

/**
 * Comparator to compare High Level Plans
 */
public class HLPlanComparator implements Comparator<HLPlan> {

    private final PlanningLevelService pls;

    public HLPlanComparator(PlanningLevelService pls){
        this.pls = pls;
    }

    @Override
    public int compare(HLPlan o1, HLPlan o2) {

        return o2.approximateSteps(pls) - o1.approximateSteps(pls);
    }
}
