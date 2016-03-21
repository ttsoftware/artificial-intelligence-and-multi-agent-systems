package dtu.agency.planners;

import dtu.agency.board.Goal;

import java.util.ArrayList;

public class HTNPlanner {

    public HTNPlanner(Goal goal) {
        
    }

    public HTNPlan plan() {
        return new HTNPlan(new ArrayList<>());
    }
}
