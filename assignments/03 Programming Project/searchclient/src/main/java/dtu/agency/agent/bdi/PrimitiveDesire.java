package dtu.agency.agent.bdi;

import dtu.agency.board.Goal;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.planners.plans.comparators.PrimitivePlanComparator;

import java.util.PriorityQueue;

public class PrimitiveDesire extends Desire<PrimitivePlan> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<PrimitivePlan> plans;

    public PrimitiveDesire(Goal goal) {
        super(goal);
        plans = new PriorityQueue<>(new PrimitivePlanComparator());
    }

    @Override
    PrimitivePlan getBest() {
        return plans.poll();
    }

    public void add(PrimitivePlan plan) {
        if (plan !=null) {
            plans.add(plan);
        }
    }
}
