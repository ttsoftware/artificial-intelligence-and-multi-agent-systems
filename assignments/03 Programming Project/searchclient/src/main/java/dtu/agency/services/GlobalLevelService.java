package dtu.agency.services;

import dtu.agency.board.*;

import java.util.List;

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
     * Have we solved all the goals?
     * @return
     */
    public synchronized boolean isAllGoalsSolved() {
        List<Goal> goals = instance.getLevel().getGoals();
        boolean isAllSolved = true;
        for (Goal goal : goals) {
            Position goalPosition = instance.getPosition(goal);
            BoardObject positionObject = instance.getObject(goalPosition);
            BoardCell positionCell = instance.getCell(goalPosition);

            if (positionCell == BoardCell.BOX_GOAL) {
                BoxAndGoal boxAndGoal = (BoxAndGoal) positionObject;
                isAllSolved &= boxAndGoal.isSolved();
            } else {
                return false;
            }
        }

        return isAllSolved;
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