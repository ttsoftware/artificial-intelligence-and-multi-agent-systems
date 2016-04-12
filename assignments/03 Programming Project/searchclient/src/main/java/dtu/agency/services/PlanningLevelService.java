package dtu.agency.services;

import dtu.agency.board.Level;

public class PlanningLevelService extends LevelService {

    public PlanningLevelService(Level level) {
        setLevel(level);
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
