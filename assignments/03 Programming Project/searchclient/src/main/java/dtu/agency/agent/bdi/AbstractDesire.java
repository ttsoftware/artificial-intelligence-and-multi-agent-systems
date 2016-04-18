package dtu.agency.agent.bdi;

import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.comparators.HLPlanComparator;
import dtu.agency.services.PlanningLevelService;

import java.util.PriorityQueue;

class AbstractDesire extends Desire<HLPlan> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<HLPlan> plans;

    public AbstractDesire(Goal goal, PlanningLevelService pls) {
        super(goal);
        plans = new PriorityQueue<>(new HLPlanComparator(pls));
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
