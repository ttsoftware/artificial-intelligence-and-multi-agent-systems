package dtu.agency.services;

import dtu.agency.board.Level;

import java.io.Serializable;

/**
 * The purpose of this PlanningLevelService is to support the planning phase of any agent
 */
public class PlanningLevelService implements Serializable {

    private Level level;

    public PlanningLevelService() {
        updateLevel();
    }

    public Level getLevel() {
        return level;
    }

    public void updateLevel() {
        this.level = GlobalLevelService.getInstance().getLevel();
    }
}
