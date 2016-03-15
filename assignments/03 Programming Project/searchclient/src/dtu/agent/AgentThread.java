package dtu.agent;

import dtu.planners.HTNPlan;
import dtu.planners.PartialOrderPlanner;
import dtu.planners.Plan;
import dtu.planners.firstorder.actions.Action;

import java.util.List;

public class AgentThread implements Runnable {

    private HTNPlan plan;

    public AgentThread(HTNPlan plan) {
        this.plan = plan;
    }

    public void performAction(Action action) {
        switch (action.getType()) {
            case MOVE: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PUSH: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PULL: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            default: { // NONE
                // Do nothing?
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        }
    }

    @Override
    public void run() {

        // partial order planning

        PartialOrderPlanner planner = new PartialOrderPlanner((HTNPlan) getPlan());
        List<Plan> popPlans = planner.plan();

        popPlans.forEach(plan1 -> {
            plan1.getActions().forEach(this::performAction);
        });
    }

    public Plan getPlan() {
        return plan;
    }
}
