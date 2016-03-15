package dtu.planners;

import java.util.List;

public class PartialOrderPlanner implements Planner {

    private HTNPlan plan;

    public PartialOrderPlanner(HTNPlan plan) {
        this.plan = plan;
    }

    @Override
    public List<Plan> plan() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
