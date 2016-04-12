package dtu.agency.services;

import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.*;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalLevelService extends LevelService implements Serializable {

    private static GlobalLevelService instance = null;

    /**
     * Gets the singleton GlobalLevelService
     * We use this singleton to store the current level instance, and to make operations on this object.
     *
     * @return GlobalLevelService
     */
    public static GlobalLevelService getInstance() {
        if (instance == null) {
            // Creating an instance must be synchronized
            synchronized (GlobalLevelService.class) {
                if (instance == null) {
                    instance = new GlobalLevelService();
                }
            }
        }
        return instance;
    }

    /**
     * Once the level has been set, it is locked to this instance.
     *
     * @param level Level
     */
    public void setLevel(Level level) {
        instance.level = instance.level == null ? level : instance.level;
    }
}