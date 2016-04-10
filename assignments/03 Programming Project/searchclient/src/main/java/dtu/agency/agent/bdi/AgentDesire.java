package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.planners.htn.PrimitivePlan;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AgentDesire { // everything the agent might want to achieve
    class PPComparator implements Comparator<PrimitivePlan> {
        @Override
        public int compare(PrimitivePlan o1, PrimitivePlan o2) {
            return o2.size() - o1.size();
        }
    }
    private HLAction intention;
    private PriorityQueue<PrimitivePlan> desires;

    public AgentDesire(HLAction intention) {
        this.intention = intention;
        desires = new PriorityQueue<>(new PPComparator());
    }

    public PrimitivePlan getNextDesire() {
        return desires.poll();
    }

    public void add(PrimitivePlan plan) {
        if (plan !=null) {
            desires.add(plan);
        }
    }
}
