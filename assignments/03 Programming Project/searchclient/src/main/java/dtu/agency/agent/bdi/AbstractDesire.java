package dtu.agency.agent.bdi;

import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.comparators.HLPlanComparator;

import java.util.PriorityQueue;

public class AbstractDesire extends Desire<HLPlan> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<HLPlan> plans;

    public AbstractDesire(Goal goal, Position agentOrigin) {
        super(goal);
        plans = new PriorityQueue<>(new HLPlanComparator(agentOrigin));
    }

    @Override
    HLPlan getBest() {
        return plans.poll();
    }

    public void add(HLPlan plan) {
        if (plan !=null) {
            plans.add(plan);
        }
    }
}
