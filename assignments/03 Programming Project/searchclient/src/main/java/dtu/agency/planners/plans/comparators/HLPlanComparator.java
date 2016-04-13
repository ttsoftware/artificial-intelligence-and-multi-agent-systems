package dtu.agency.planners.plans.comparators;

import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;

import java.util.Comparator;

/**
 * Created by koeus on 4/13/16.
 */
public class HLPlanComparator implements Comparator<HLPlan> {

    Position agentOrigin;

    public HLPlanComparator(Position agentOrigin){
        this.agentOrigin = agentOrigin;
    }

    @Override
    public int compare(HLPlan o1, HLPlan o2) {

        return o2.approximateSteps(agentOrigin) - o1.approximateSteps(agentOrigin);
    }
}
