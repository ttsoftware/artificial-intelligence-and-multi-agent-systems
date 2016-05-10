package dtu.agency.services;

import dtu.agency.board.Level;

public class GlobalLevelService extends LevelService {

    private static GlobalLevelService instance = null;

    /**
     * Gets the singleton GlobalLevelService
     * We use this singleton to store the current level instance, and to make operations on this object.
     *
     * @return GlobalLevelService
     */
    public static synchronized GlobalLevelService getInstance() {
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
        instance.level = level;
    }
}