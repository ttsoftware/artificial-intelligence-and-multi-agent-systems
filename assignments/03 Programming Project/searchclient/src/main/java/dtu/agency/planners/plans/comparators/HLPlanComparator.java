package dtu.agency.planners.plans.comparators;

import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;

import java.util.Comparator;

/**
 * Comparator to compare High Level Plans
 */
public class HLPlanComparator implements Comparator<HLPlan> {

    private final Position agentOrigin;

    public HLPlanComparator(Position agentOrigin){
        this.agentOrigin = agentOrigin;
    }

    @Override
    public int compare(HLPlan o1, HLPlan o2) {

        return o2.approximateSteps(agentOrigin) - o1.approximateSteps(agentOrigin);
    }
}
