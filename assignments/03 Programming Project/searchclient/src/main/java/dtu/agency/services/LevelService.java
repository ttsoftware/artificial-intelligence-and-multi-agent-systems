package dtu.agency.services;

import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.*;

import java.io.Serializable;
import java.util.Hashtable;

public class LevelService implements Serializable {

    private static LevelService instance = null;
    private Level level = null;

    private LevelService() {
    }

    /**
     * Gets the singleton LevelService
     * We use this singleton to store the current level instance, and to make operations on this object.
     * @return LevelService
     */
    public static LevelService getInstance() {
        if (instance == null) {
            // Creating an instance must be synchronized
            synchronized (LevelService.class) {
                if (instance == null) {
                    instance = new LevelService();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param agent
     * @param action
     * @return Whether or not the action was performed
     */
    public synchronized boolean move(Agent agent, MoveAction action) {
        // We must synchronize here to avoid collisions.
        // Do we want to handle conflicts in this step/class?

        BoardCell[][] boardState = level.getBoardState();
        Hashtable<String, Position> objectPositions = level.getBoardObjectPositions();

        // find the object corresponding to this agent
        Position position = objectPositions.get(agent.getLabel());

        // move the agent to the new position
        try {
            switch (action.getDirection()) {
                case NORTH:
                    boardState[position.getRow()-1][position.getColumn()] = BoardCell.AGENT;
                    break;
                case SOUTH:
                    boardState[position.getRow()+1][position.getColumn()] = BoardCell.AGENT;
                    break;
                case EAST:
                    boardState[position.getRow()][position.getColumn()+1] = BoardCell.AGENT;
                    break;
                case WEST:
                    boardState[position.getRow()][position.getColumn()-1] = BoardCell.AGENT;
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // TODO: Handle this better
            e.printStackTrace();
            return false;
        }

        // free the cell where the agent is currently located
        boardState[position.getRow()][position.getColumn()] = BoardCell.FREE_CELL;

        return true;
    }

    public synchronized boolean push(Agent agent, PushAction action) {
        return false;
    }

    public synchronized boolean pull(Agent agent, PullAction action) {
        return false;
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