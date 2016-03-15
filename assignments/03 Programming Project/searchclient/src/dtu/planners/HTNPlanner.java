package dtu.planners;

import dtu.board.Level;

import java.util.List;

public class HTNPlanner implements Planner {

    private Level level;

    public HTNPlanner(Level level) {
        this.level = level;
    }

    @Override
    public List<Plan> plan() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
