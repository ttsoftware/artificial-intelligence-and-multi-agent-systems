package dtu.agency.services;

import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by koeus on 4/8/16.
 * I think that the purpose of this PlanningLevelService is to support the planning phase of any agent
 */
public class PlanningLevelService implements Serializable {

    private Level level;
    private HashMap<Box, Position> boxes;
    private int agent;

    public PlanningLevelService(int agent) {
        this.agent = agent;
    }

    public Level getLevel() {
        return level;
    }

    public void updateLevel() {
        this.level = GlobalLevelService.getInstance().getLevel();
    }
}
