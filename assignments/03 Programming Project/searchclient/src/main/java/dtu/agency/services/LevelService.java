package dtu.agency.services;

import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;

import java.io.Serializable;

public class LevelService implements Serializable {

    private static LevelService instance = null;
    private Level level = null;

    protected LevelService() {
    }

    /**
     * Gets the singleton LevelService
     * We use this singleton to store the current level instance, and to make operations on this object.
     * @return LevelService
     */
    public static LevelService getInstance() {
        if (instance == null) {
            // All calls to getInstance() needs to wait for each other
            synchronized (LevelService.class) {
                if (instance == null) {
                    instance = new LevelService();
                }
            }
        }
        return instance;
    }

    public void move(Agent agent, MoveAction action) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void push(Agent agent, PullAction action) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void pull(Agent agent, PushAction action) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Once the level has been set, it is locked to this instance.
     * @param level Level
     */
    public void setLevel(Level level) {
        instance.level = instance.level == null ? level : instance.level;
    }

    public Level getLevel() {
        return instance.level;
    }
}