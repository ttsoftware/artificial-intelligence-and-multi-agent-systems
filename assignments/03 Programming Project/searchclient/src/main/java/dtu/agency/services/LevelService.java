package dtu.agency.services;

import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LevelService implements Serializable {

    private static LevelService instance = null;
    private Level level = null;

    private LevelService() {
    }

    /**
     * Gets the singleton LevelService
     * We use this singleton to store the current level instance, and to make operations on this object.
     *
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

    public synchronized boolean move(Agent agent, MoveAction action) {
        // We must synchronize here to avoid collisions.
        // Do we want to handle conflicts in this step/class?

        return moveObject(agent, action.getDirection());
    }

    public synchronized boolean push(Agent agent, PushAction action) {
        // move the box to the new position
        boolean moveSuccess = moveObject(action.getBox(), action.getBoxDirection());

        if (moveSuccess) {
            // if we could move the box, we can also move the agency to the new position
            moveObject(agent, action.getAgentDirection());
        }

        return moveSuccess;
    }

    public synchronized boolean pull(Agent agent, PullAction action) {
        // move the agency to the new position
        boolean moveSuccess = moveObject(agent, action.getAgentDirection());

        if (moveSuccess) {
            moveSuccess = moveObject(action.getBox(), action.getBoxDirection());

            if (!moveSuccess) {
                // if we could not move the box, we have to move the agency back
                moveObject(agent, action.getAgentDirection().getInverse());
            }
        }

        return moveSuccess;
    }

    /**
     * Move a single BoardObject into a new position
     *
     * @param boardObject
     * @param direction
     * @return
     */
    private synchronized boolean moveObject(BoardObject boardObject, Direction direction) {

        BoardCell[][] boardState = level.getBoardState();
        Hashtable<String, Position> objectPositions = level.getBoardObjectPositions();

        // find the object
        Position position = objectPositions.get(boardObject.getLabel());

        // find the object type
        BoardCell boardCell = boardState[position.getRow()][position.getColumn()];

        int nextRow = -1;
        int nextColumn = -1;

        // move the object to the new position
        switch (direction) {
            case NORTH: {
                nextRow = position.getRow();
                nextColumn = position.getColumn() - 1;
                if (causesCollision(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case SOUTH: {
                nextRow = position.getRow() + 1;
                nextColumn = position.getColumn();
                if (causesCollision(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case EAST: {
                nextRow = position.getRow();
                nextColumn = position.getColumn() + 1;
                if (causesCollision(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case WEST: {
                nextRow = position.getRow();
                nextColumn = position.getColumn() - 1;
                if (causesCollision(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
        }

        // free the cell where the object was located
        boardState[position.getRow()][position.getColumn()] = BoardCell.FREE_CELL;
        objectPositions.remove(boardObject.getLabel());
        objectPositions.put(boardObject.getLabel(), new Position(nextRow, nextColumn));

        // update the level object
        level.setBoardState(boardState);
        level.setBoardObjectPositions(objectPositions);

        return true;
    }

    private boolean causesCollision(int row, int column) {
        BoardCell nextCell = level.getBoardState()[row][column];

        switch (nextCell) {
            case FREE_CELL:
                // No collision
                return false;
            case GOAL:
                // No collision
                return false;
            default:
                // All other cases are collisions
                break;
        }

        return true;
    }

    public List<Neighbour> getFreeNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (LevelService.getInstance().isFreeNeighbour(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(new Position(position.getRow(), position.getColumn() - 1), Direction.WEST));
        }
        if (LevelService.getInstance().isFreeNeighbour(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(new Position(position.getRow(), position.getColumn() + 1), Direction.EAST));
        }
        if (LevelService.getInstance().isFreeNeighbour(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(new Position(position.getRow() - 1, position.getColumn()), Direction.NORTH));
        }
        if (LevelService.getInstance().isFreeNeighbour(position.getRow() + 1, position.getColumn())) {
            neighbours.add(new Neighbour(new Position(position.getRow() + 1, position.getColumn()), Direction.SOUTH));
        }

        return neighbours;
    }

    public Position getPositionInDirection(Position currentPosition, Direction movingDirection) {
        switch (movingDirection) {
            case NORTH:
                return new Position(currentPosition.getRow() - 1, currentPosition.getColumn());
            case SOUTH:
                return new Position(currentPosition.getRow() + 1, currentPosition.getColumn());
            case WEST:
                return new Position(currentPosition.getRow(), currentPosition.getColumn() - 1);
            case EAST:
                return new Position(currentPosition.getRow(), currentPosition.getColumn() + 1);
            default:
                return null;
        }
    }

    public Direction getMovingDirection(Position positionOne, Position positionTwo) {
        if (positionOne.getRow() == positionTwo.getRow()) {
            if (positionOne.getColumn() < positionTwo.getColumn()) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else if (positionOne.getColumn() == positionTwo.getColumn()) {
            if (positionOne.getRow() > positionTwo.getRow()) {
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        }
        return null;
    }

    public boolean isFreeNeighbour(int row, int column) {
        if (row >= 0 && column >= 0 && level.getBoardState().length > row && level.getBoardState()[0].length > column) {
            if (level.getBoardState()[row][column].equals(BoardCell.FREE_CELL)
                    || level.getBoardState()[row][column].equals(BoardCell.GOAL)) {
                return true;
            }
        }
        return false;
    }

    public Position getPosition(String objectLabel) {
        return level.getBoardObjectPositions().get(objectLabel);
    }

    /**
     * We use Manhattan distances to define "closeness"
     *
     * @param agent
     * @param goal
     * @return The box closest to @agent which solves @goal
     */
    public Box closestBox(Agent agent, Goal goal) {

        int shortestDistance = Integer.MAX_VALUE;
        Box shortestDistanceBox = null;

        // Find the closest box which solves @goal
        for (Box box : level.getGoalsBoxes().get(goal.getLabel())) {
            // boxes associated with @goal
            int distance = manhattanDistance(box, agent);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                shortestDistanceBox = box;
            }
        }

        return shortestDistanceBox;
    }

    /**
     * Manhattan distance: <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">https://en.wikipedia.org/wiki/Taxicab_geometry</a>
     * <p>
     * Should have Expected O(1) time complexity.
     *
     * @param objectA
     * @param objectB
     * @return The manhattan distance between the two objects
     */
    public int manhattanDistance(BoardObject objectA, BoardObject objectB) {

        // E[O(1)] time operations
        Position positionA = level.getBoardObjectPositions().get(objectA.getLabel());
        Position positionB = level.getBoardObjectPositions().get(objectB.getLabel());

        // O(1) time operations
        return manhattanDistance(positionA, positionB);
    }

    /**
     * Manhattan distance: <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">https://en.wikipedia.org/wiki/Taxicab_geometry</a>
     * <p>
     * Should have Expected O(1) time complexity.
     *
     * @param positionA
     * @param positionB
     * @return The manhattan distance between the two objects
     */
    public int manhattanDistance(Position positionA, Position positionB) {
        // O(1) time operations
        return Math.abs(positionA.getRow() - positionB.getRow())
                + Math.abs(positionA.getColumn() - positionB.getColumn());
    }

    /**
     * Once the level has been set, it is locked to this instance.
     *
     * @param level Level
     */
    public void setLevel(Level level) {
        instance.level = instance.level == null ? level : instance.level;
    }

    public Level getLevel() {
        return instance.level;
    }
}