package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.planners.plans.comparators.PrimitivePlanComparator;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AgentDesire { // everything the agent might want to achieve

    private HLAction intention;
    // should one know the initial position of this plan??
    private PriorityQueue<PrimitivePlan> desires;

    public AgentDesire(HLAction intention) {
        this.intention = intention;
        desires = new PriorityQueue<>(new PrimitivePlanComparator());
    }

    public PrimitivePlan getBestDesire() {
        return desires.poll();
    }

    public void add(PrimitivePlan plan) {
        if (plan !=null) {
            desires.add(plan);
        }
    }
}
