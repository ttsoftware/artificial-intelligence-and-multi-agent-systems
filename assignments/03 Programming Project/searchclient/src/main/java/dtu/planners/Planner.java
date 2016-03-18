package dtu.planners;

import dtu.Level;

import java.util.List;

public abstract class Planner {

    protected Level level;

    public Planner(Level level) {
        this.level = level;
    }

    public abstract List<Plan> plan();
}
