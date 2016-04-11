package dtu.agency.services;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;

import java.io.Serializable;

/**
 * The purpose of this PlanningLevelService is to support the planning phase of any agent
 */
public class PlanningLevelService implements Serializable {
    private static Agent agent;
    private static Box targetBox;
    private Level level;

    public PlanningLevelService(Box target) {
        updateLevel();
        agent = BDIService.getAgent();
        targetBox = target;
    }

    public static Agent getAgent() { return agent; }

    public static Box getTargetBox() { return targetBox; }

    public static void setTargetBox(Box targetBox) {
        PlanningLevelService.targetBox = targetBox;
    }

    public Level getLevel() {
        return level;
    }

    public void updateLevel() {
        this.level = GlobalLevelService.getInstance().getLevel();
    }
}
