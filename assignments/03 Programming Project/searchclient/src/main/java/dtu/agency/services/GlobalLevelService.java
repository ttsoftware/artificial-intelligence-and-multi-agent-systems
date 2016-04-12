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

public class GlobalLevelService implements Serializable {

    private static GlobalLevelService instance = null;
    private Level level = null;

    private GlobalLevelService() {
    }

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

    public synchronized boolean move(Agent agent, MoveConcreteAction action) {
        // We must synchronize here to avoid collisions.
        // Do we want to handle conflicts in this step/class?

        return moveObject(agent, action.getDirection());
    }

    public synchronized boolean push(Agent agent, PushConcreteAction action) {
        // move the box to the new position
        boolean moveSuccess = moveObject(action.getBox(), action.getBoxDirection());

        if (moveSuccess) {
            // if we could move the box, we can also move the agency to the new position
            moveObject(agent, action.getAgentDirection());
        }

        return moveSuccess;
    }

    public synchronized boolean pull(Agent agent, PullConcreteAction action) {
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
        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();

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
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case SOUTH: {
                nextRow = position.getRow() + 1;
                nextColumn = position.getColumn();
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case EAST: {
                nextRow = position.getRow();
                nextColumn = position.getColumn() + 1;
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                boardState[nextRow][nextColumn] = boardCell;
                break;
            }
            case WEST: {
                nextRow = position.getRow();
                nextColumn = position.getColumn() - 1;
                if (!isFree(nextRow, nextColumn)) {
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

    /**
     * @param position
     * @return A list of adjacent cells containing a box or an agent
     */
    public synchronized List<Neighbour> getMoveableNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (GlobalLevelService.getInstance().isMoveable(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (GlobalLevelService.getInstance().isMoveable(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (GlobalLevelService.getInstance().isMoveable(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (GlobalLevelService.getInstance().isMoveable(position.getRow() + 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() + 1, position.getColumn()),
                    Direction.SOUTH
            ));
        }

        return neighbours;
    }

    /**
     * @param position
     * @return A list of free cells adjacent to @position
     */
    public synchronized List<Neighbour> getFreeNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (GlobalLevelService.getInstance().isFree(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (GlobalLevelService.getInstance().isFree(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (GlobalLevelService.getInstance().isFree(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (GlobalLevelService.getInstance().isFree(position.getRow() + 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() + 1, position.getColumn()),
                    Direction.SOUTH
            ));
        }

        return neighbours;
    }

    /**
     * @param currentPosition
     * @param movingDirection
     * @return The position arrived at after moving from @currentPosition in @movingDirection
     */
    public synchronized Position getAdjacentPositionInDirection(Position currentPosition, Direction movingDirection) {
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


    public Direction getDirectionToBox(Position agentPosition, Position boxPosition) { // returns the direction from agent to box
        if (agentPosition.getRow() == boxPosition.getRow()) {
            if (agentPosition.getColumn() < boxPosition.getColumn()) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else if (agentPosition.getColumn() == boxPosition.getColumn()) {
            if (agentPosition.getRow() > boxPosition.getRow()) {
                return Direction.NORTH;
            } else {
                return Direction.SOUTH;
            }
        }
        throw new InvalidParameterException("Given positions are not adjacent.");
    }

    /**
     * @param positionA
     * @param positionB
     * @param reverse Whether to return the inverse direction
     * @return The direction of positionB relative to positionA
     */
    public synchronized Direction getRelativeDirection(Position positionA, Position positionB, boolean reverse) {
        if (positionA.getRow() == positionB.getRow()) {
            if (positionA.getColumn() < positionB.getColumn()) {
                return !reverse ? Direction.EAST : Direction.WEST;
            } else {
                return !reverse ? Direction.WEST : Direction.EAST;
            }
        } else if (positionA.getColumn() == positionB.getColumn()) {
            if (positionA.getRow() > positionB.getRow()) {
                return !reverse ? Direction.NORTH : Direction.SOUTH;
            } else {
                return !reverse ? Direction.SOUTH : Direction.NORTH;
            }
        }
        throw new InvalidParameterException("Given positions are not adjacent.");
    }

    /**
     * @param position
     * @return True if object at given position can be moved
     */
    public synchronized boolean isMoveable(Position position) {
        return isMoveable(position.getRow(), position.getColumn());
    }

    /**
     * @param row
     * @param column
     * @return True if object at given position can be moved
     */
    public synchronized boolean isMoveable(int row, int column) {
        if (isInLevel(row, column)) {
            if (level.getBoardState()[row][column].equals(BoardCell.AGENT)
                    || level.getBoardState()[row][column].equals(BoardCell.AGENT_GOAL)
                    || level.getBoardState()[row][column].equals(BoardCell.BOX_GOAL)
                    || level.getBoardState()[row][column].equals(BoardCell.BOX)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param position
     * @return True if the given position is free
     */
    public synchronized boolean isFree(Position position) {
        return isFree(position.getRow(), position.getColumn());
    }

    /**
     * @param row
     * @param column
     * @return True if the given position is free
     */
    public synchronized boolean isFree(int row, int column) {
        if (isInLevel(row, column)) {
            BoardCell cell = level.getBoardState()[row][column];
            if (cell.equals(BoardCell.FREE_CELL)
                    || cell.equals(BoardCell.GOAL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param position
     * @return True if a wall exists at given position
     */
    public synchronized boolean isWall(Position position) {
        return isWall(position.getRow(), position.getColumn());
    }

    /**
     * @param row
     * @param column
     * @return True if a wall exists at given position
     */
    public synchronized boolean isWall(int row, int column) {
        return level.getBoardState()[row][column] == BoardCell.WALL;
    }

    public synchronized boolean isAgent(Position pos) {
        return isAgent(pos.getRow(), pos.getColumn());
    }

    public synchronized boolean isAgent(int row, int column) {
        boolean value = level.getBoardState()[row][column] == BoardCell.AGENT;
        value |= level.getBoardState()[row][column] == BoardCell.AGENT_GOAL;
        return value;
    }

    public synchronized String getObjectLabels(Position pos) {
        return level.getBoardObjects()[pos.getRow()][pos.getColumn()].getLabel();
    }

        public synchronized Position getPosition(BoardObject boardObject) {
        return getPosition(boardObject.getLabel());
    }

    public synchronized Position getPosition(String objectLabel) {
        return level.getBoardObjectPositions().get(objectLabel);
    }

    /**
     * We use Manhattan distances to define "closeness"
     *
     * @param agent
     * @param goal
     * @return The box closest to @agent which solves @goal
     */
    public synchronized Box closestBox(Agent agent, Goal goal) {

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
     *
     * @param boardObjectA
     * @param boardObjectB
     * @return The euclidean distance from @boardObjectA to @boardObjectB
     */
    public synchronized int euclideanDistance(BoardObject boardObjectA, BoardObject boardObjectB) {
        return euclideanDistance(
                getPosition(boardObjectA.getLabel()),
                getPosition(boardObjectB.getLabel())
        );
    }

    /**
     *
     * @param positionA
     * @param positionB
     * @return The euclidean distance from @positionA to @positionB
     */
    public synchronized int euclideanDistance(Position positionA, Position positionB) {
        return (int) Math.round(
                Math.sqrt(
                        (positionA.getRow() - positionB.getRow()) ^ 2
                                + (positionA.getColumn() - positionB.getColumn()) ^ 2
                )
        );
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
    public synchronized int manhattanDistance(BoardObject objectA, BoardObject objectB) {

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
    public synchronized int manhattanDistance(Position positionA, Position positionB) {
        // O(1) time operations
        return Math.abs(positionA.getRow() - positionB.getRow())
                + Math.abs(positionA.getColumn() - positionB.getColumn());
    }

    /**
     * @param position
     * @return True if the position exists in the level
     */
    public synchronized boolean isInLevel(Position position) {
        return isInLevel(position.getRow(), position.getColumn());
    }

    /**
     * @param row
     * @param column
     * @return True if the position exists in the level
     */
    public synchronized boolean isInLevel(int row, int column) {
        return (row >= 0
                && column >= 0
                && level.getBoardState().length > row
                && level.getBoardState()[0].length > column);
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