package dtu.agency.services;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.board.*;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.PrimitivePlan;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public abstract class LevelService {

    protected Level level;

    public Level getLevel() {
        return level;
    }

    /**
     * @return A clone of the level object
     */
    public Level getLevelClone() {
        return new Level(level);
    }

    public synchronized boolean applyAction(Agent agent, ConcreteAction action) {
        switch (action.getType()) {
            case MOVE:
                return move(agent, (MoveConcreteAction) action);
            case PUSH:
                return push(agent, (PushConcreteAction) action);
            case PULL:
                return pull(agent, (PullConcreteAction) action);
            default:
                return false;
        }
    }

    public synchronized boolean move(Agent agent, MoveConcreteAction action) {
        // We must synchronize here to avoid collisions.
        return moveObject(agent, action.getDirection());
    }

    public synchronized boolean push(Agent agent, PushConcreteAction action) {
        // move the box to the new position
        boolean moveSuccess = moveObject(action.getBox(), action.getBoxMovingDirection());

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
            moveSuccess = moveObject(action.getBox(), action.getBoxMovingDirection());

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
    protected synchronized boolean moveObject(BoardObject boardObject, Direction direction) {

        BoardCell[][] boardState = level.getBoardState();
        BoardObject[][] boardObjects = level.getBoardObjects();
        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();

        // find the object
        Position position = objectPositions.get(boardObject.getLabel());
        int row = position.getRow();
        int column = position.getColumn();

        // find the object type
        BoardCell currentCell = boardState[row][column];
        BoardObject currentObject = boardObjects[row][column];
        int nextRow = -1;
        int nextColumn = -1;

        // move the object to the new position
        switch (direction) {
            case NORTH: {
                nextRow = row - 1;
                nextColumn = column;
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                break;
            }
            case SOUTH: {
                nextRow = row + 1;
                nextColumn = column;
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                break;
            }
            case EAST: {
                nextRow = row;
                nextColumn = column + 1;
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                break;
            }
            case WEST: {
                nextRow = row;
                nextColumn = column - 1;
                if (!isFree(nextRow, nextColumn)) {
                    // We cannot perform this action
                    return false;
                }
                break;
            }
        }

        // Cell at the next position
        BoardCell nextCell = boardState[nextRow][nextColumn];
        // Object at the next position
        BoardObject nextObject = boardObjects[nextRow][nextColumn];

        // update next board cell
        if (nextCell == BoardCell.GOAL) {
            // handles cases where objects enters a goal cell
            switch (currentCell) {
                case BOX:
                    boardState[nextRow][nextColumn] = BoardCell.BOX_GOAL;
                    boardObjects[nextRow][nextColumn] = new BoxAndGoal((Box) currentObject, (Goal) nextObject);
                    break;
                case AGENT:
                    boardState[nextRow][nextColumn] = BoardCell.AGENT_GOAL;
                    boardObjects[nextRow][nextColumn] = new AgentAndGoal((Agent) currentObject, (Goal) nextObject);
                    break;
                case AGENT_GOAL:
                    boardState[nextRow][nextColumn] = BoardCell.AGENT_GOAL;
                    boardObjects[nextRow][nextColumn] = new AgentAndGoal(
                            ((AgentAndGoal) currentObject).getAgent(),
                            (Goal) nextObject
                    );
                    break;
                case BOX_GOAL:
                    boardState[nextRow][nextColumn] = BoardCell.BOX_GOAL;
                    boardObjects[nextRow][nextColumn] = new BoxAndGoal(
                            ((BoxAndGoal) currentObject).getBox(),
                            (Goal) nextObject
                    );
                    break;
                default:
                    throw new RuntimeException("We cannot move walls, goals or air.");
            }
        } else if (currentCell == BoardCell.AGENT_GOAL) {
            // If the current cell is an agent goal and the next cell is free
            boardState[nextRow][nextColumn] = BoardCell.AGENT;
            boardObjects[nextRow][nextColumn] = ((AgentAndGoal) currentObject).getAgent();
        } else if (currentCell == BoardCell.BOX_GOAL) {
            // If the current cell is a box goal and the next cell is free
            boardState[nextRow][nextColumn] = BoardCell.BOX;
            boardObjects[nextRow][nextColumn] = ((BoxAndGoal) currentObject).getBox();
        } else {
            // If the next cell is free
            boardState[nextRow][nextColumn] = currentCell;
            boardObjects[nextRow][nextColumn] = currentObject;
        }

        // free the cell where the object was located
        if (currentCell == BoardCell.AGENT_GOAL) {
            boardState[row][column] = BoardCell.GOAL;
            boardObjects[row][column] = ((AgentAndGoal) currentObject).getGoal();
        } else if (currentCell == BoardCell.BOX_GOAL) {
            boardState[row][column] = BoardCell.GOAL;
            boardObjects[row][column] = ((BoxAndGoal) currentObject).getGoal();
        } else {
            boardState[row][column] = BoardCell.FREE_CELL;
            boardObjects[row][column] = new Empty(" ");
        }

        objectPositions.remove(boardObject.getLabel());
        objectPositions.put(boardObject.getLabel(), new Position(nextRow, nextColumn));

        // update the level object
        level.setBoardState(boardState);
        level.setBoardObjects(boardObjects);
        level.setBoardObjectPositions(objectPositions);

        return true;
    }

    /**
     * @param label
     * @return The agent associated with the given label
     */
    public Agent getAgent(String label) {
        return level.getAgentsMap().get(label);
    }

    /**
     * @param agentNumber
     * @return The agent associated with the given number
     */
    public Agent getAgent(Integer agentNumber) {
        // There should only be 1 agent with this number
        return level.getAgents()
                .stream()
                .filter(agent -> agent.getNumber() == agentNumber)
                .collect(Collectors.toList()).get(0);
    }

    public synchronized List<Neighbour> getGoalFreeNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (level.getBoardState()[position.getRow()][position.getColumn() - 1].equals(BoardCell.GOAL)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (level.getBoardState()[position.getRow()][position.getColumn() + 1].equals(BoardCell.GOAL)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }

        if (level.getBoardState()[position.getRow() - 1][position.getColumn()].equals(BoardCell.GOAL)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }

        if (level.getBoardState()[position.getRow() + 1][position.getColumn()].equals(BoardCell.GOAL)) {
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
     * @param reverse   Whether to return the inverse direction
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
     * @param row
     * @param column
     * @return True if the given position is free
     */
    public synchronized boolean isFree(int row, int column) {
        if (isInLevel(row, column)) {
            if (level.getBoardState()[row][column].equals(BoardCell.FREE_CELL)
                    || level.getBoardState()[row][column].equals(BoardCell.GOAL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param row
     * @param column
     * @return True if the given position doesn't have a goal in it
     */
    public synchronized boolean isFreeOfGoals(int row, int column) {
        if (isInLevel(row, column)) {
            BoardCell boardCell = level.getBoardState()[row][column];
            if (boardCell.equals(BoardCell.WALL)
                    || boardCell.equals(BoardCell.GOAL)
                    || boardCell.equals(BoardCell.BOX_GOAL)
                    || boardCell.equals(BoardCell.AGENT_GOAL)) {
                return false;
            }
        }
        return true;
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
     * @param objectsToIgnore
     * @return True if the given position is free or if it contains one object from the objectsToIgnore list
     */
    public synchronized boolean isFree(int row, int column, List<BoardObject> objectsToIgnore) {
        if (isInLevel(row, column)) {
            BoardCell cell = level.getBoardState()[row][column];
            if (cell.equals(BoardCell.FREE_CELL)
                    || cell.equals(BoardCell.GOAL)) {
                return true;
            } else {
                for (BoardObject objectToIgnore : objectsToIgnore) {
                    if (level.getBoardObjects()[row][column] != null) {
                        if (level.getBoardObjects()[row][column].getLabel().equals(objectToIgnore.getLabel())) {
                            return true;
                        }
                    }
                }
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

    public synchronized String getObjectLabel(Position pos) {
        return level.getBoardObjects()[pos.getRow()][pos.getColumn()].getLabel();
    }

    public synchronized BoardObject getObject(Position pos) {
        return level.getBoardObjects()[pos.getRow()][pos.getColumn()];
    }

    public synchronized BoardCell getCell(Position pos) {
        return level.getBoardState()[pos.getRow()][pos.getColumn()];
    }

    public synchronized Position getPosition(BoardObject boardObject) {
        return getPosition(boardObject.getLabel());
    }

    public synchronized Position getPosition(String objectLabel) {
        return level.getBoardObjectPositions().get(objectLabel);
    }

    public synchronized void updatePriorityQueues(List<PriorityBlockingQueue<Goal>> goalQueueList) {
        level.setGoalQueues(goalQueueList);
    }

    public synchronized List<PriorityBlockingQueue<Goal>> getPriorityQueuesClone() {
        List<PriorityBlockingQueue<Goal>> queueListClone = new ArrayList<>();
        level.getGoalQueues().forEach(goalsQueue -> {
            queueListClone.add(new PriorityBlockingQueue(goalsQueue));
        });

        return queueListClone;
    }

    /**
     * Insert a box into the level
     * Usage: when returning responsibility of the box to this levelservice
     *
     * @param box      Box to insert into level
     * @param position Position to insert the box in the level
     */
    protected synchronized void insertBox(Box box, Position position) {
        int row = position.getRow();
        int column = position.getColumn();

        BoardCell[][] boardState = level.getBoardState();
        BoardCell cell = boardState[row][column];

        // update the cell where the box is now located
        switch (cell) {
            case FREE_CELL:
                boardState[row][column] = BoardCell.BOX;
                break;
            case GOAL:
                boardState[row][column] = BoardCell.BOX_GOAL;
                break;
            default:
                // we have not been able to find the box
                // (agent?) blocking the goal, when looking for obstacles??
                // agents should move upon conflict resolution.
                throw new NotAFreeCellException("Cannot insert box on any cell but FREE or GOAL cells");
        }
        level.setBoardState(boardState);

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(box.getLabel()) != null)
            throw new AssertionError("Expected the box NOT to exist in the level");
        objectPositions.put(box.getLabel(), new Position(row, column));
        level.setBoardObjectPositions(objectPositions);

        List<Box> boxes = level.getBoxes();
        if (boxes.contains(box))
            throw new AssertionError("Box should not exist in level before adding it");
        boxes.add(box);
        level.setBoxes(boxes);

        BoardObject[][] boardObjects = level.getBoardObjects();
        if (cell == BoardCell.GOAL) {
            boardObjects[row][column] = new BoxAndGoal(
                    box,
                    (Goal) getObject(position)
            );
        } else {
            boardObjects[row][column] = box;
        }
        level.setBoardObjects(boardObjects);
    }

    /**
     * Insert an agent into the level at a given position
     * Usage: when responsibility of agent is returned to level
     *
     * @param agent    Agent to insert into level
     * @param position Position to insert the agent
     */
    protected synchronized void insertAgent(Agent agent, Position position) {
        int row = position.getRow();
        int column = position.getColumn();

        BoardCell[][] boardState = level.getBoardState();
        BoardCell cell = boardState[row][column];

        // update the cell where the agent is now located
        switch (cell) {
            case FREE_CELL:
                boardState[row][column] = BoardCell.AGENT;
                break;
            case GOAL:
                boardState[row][column] = BoardCell.AGENT_GOAL;
                break;
            case AGENT:
                // TODO: Some other agent is standing here
                boardState[row][column] = BoardCell.AGENT;
                break;
            default:
                // System.err.println("Agent " + BDIService.getInstance().getAgent() + " is trying to insert " + agent + " at position " + position + " where there is a " + cell.toString());
                throw new AssertionError("Cannot insert agent on any cell but FREE or GOAL cells");
        }
        level.setBoardState(boardState);

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(agent.getLabel()) != null)
            throw new AssertionError("Expected the agent NOT to exist in the level");
        objectPositions.put(agent.getLabel(), new Position(row, column));
        level.setBoardObjectPositions(objectPositions);

        List<Agent> agents = level.getAgents();
        if (agents.contains(agent))
            throw new AssertionError("Agent should not exist in level before adding it");
        agents.add(agent);
        level.setAgents(agents);

        BoardObject[][] boardObjects = level.getBoardObjects();
        if (cell == BoardCell.GOAL) {
            boardObjects[row][column] = new AgentAndGoal(
                    agent,
                    (Goal) getObject(position)
            );
        } else {
            boardObjects[row][column] = agent;
        }
        level.setBoardObjects(boardObjects);
    }

    /**
     * @param position
     * @WARNING: Removes anything from this position!
     * We have no way of knowing what was removed, so we cannot put it back
     */
    protected synchronized void clearPosition(Position position) {
        BoardCell[][] boardState = level.getBoardState();
        BoardObject[][] boardObjects = level.getBoardObjects();

        boardState[position.getRow()][position.getColumn()] = BoardCell.FREE_CELL;
        boardObjects[position.getRow()][position.getColumn()] = new Empty(" ");

        level.setBoardState(boardState);
        level.setBoardObjects(boardObjects);
    }

    /**
     * Removing a box from a level
     * Usage: when assuming control and responsibility for that box in the level
     *
     * @param box Box to remove from the level
     */
    protected synchronized void removeBox(Box box) {
        Position boxPos = getPosition(box);
        int row = boxPos.getRow();
        int column = boxPos.getColumn();

        BoardCell[][] boardState = level.getBoardState();
        BoardCell cell = boardState[row][column];

        switch (cell) {
            case BOX:
                boardState[row][column] = BoardCell.FREE_CELL;
                break;
            case BOX_GOAL:
                boardState[row][column] = BoardCell.GOAL;
                break;
            default:
                Agent ag = BDIService.getInstance().getAgent();
                String sa = "Agent " + ag + ": ";
                // System.err.println(sa + "lvl: agents: " + level.getAgents() + " boxes: " + level.getBoxes());
                // System.err.println(sa + "lvl: objectPositions: " + level.getBoardObjectPositions());
                throw new AssertionError("Cannot remove box if not present");
        }
        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(box.getLabel()) == null)
            throw new AssertionError("Cannot remove non-existing box");
        objectPositions.remove(box.getLabel());
        level.setBoardObjectPositions(objectPositions);

        ArrayList<Box> boxes = new ArrayList<>(level.getBoxes());
        if (!boxes.contains(box))
            throw new AssertionError("Box should exist in level before removing it");
        boxes.remove(box);
        level.setBoxes(boxes);

        BoardObject[][] boardObjects = level.getBoardObjects();
        if (cell == BoardCell.BOX_GOAL) {
            boardObjects[row][column] = ((BoxAndGoal) getObject(boxPos)).getGoal();
        } else {
            boardObjects[row][column] = new Empty(" ");
        }
        level.setBoardObjects(boardObjects);
    }

    /**
     * Remove an agent from the level
     * Usage: when assuming responsibility of agent from the level
     *
     * @param agent Agent to remove from level
     */
    protected synchronized void removeAgent(Agent agent) {

        Position agentPos = getPosition(agent);
        int row = agentPos.getRow();
        int column = agentPos.getColumn();

        BoardCell[][] boardCells = level.getBoardState();
        BoardCell cell = boardCells[row][column];
        switch (cell) {
            case AGENT:
                boardCells[row][column] = BoardCell.FREE_CELL;
                break;
            case AGENT_GOAL:
                boardCells[row][column] = BoardCell.GOAL;
                break;
            default:
                throw new AssertionError("Cannot remove agent if not present");
        }

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(agent.getLabel()) == null)
            throw new AssertionError("Cannot remove non-existing agent");
        objectPositions.remove(agent.getLabel());
        level.setBoardObjectPositions(objectPositions);

        List<Agent> agents = level.getAgents();
        if (!agents.contains(agent))
            throw new AssertionError("Agent should exist in level before removing it");
        agents.remove(agent);
        level.setAgents(agents);

        BoardObject[][] boardObjects = level.getBoardObjects();
        if (cell == BoardCell.AGENT_GOAL) {
            boardObjects[row][column] = ((AgentAndGoal) getObject(agentPos)).getGoal();
        } else {
            boardObjects[row][column] = new Empty(" ");
        }
        level.setBoardState(boardCells);
        level.setBoardObjects(boardObjects);
    }

    /**
     * @param boardObjectA
     * @param boardObjectB
     * @return The euclidean distance from @boardObjectA to @boardObjectB
     */
    public synchronized int euclideanDistance(BoardObject boardObjectA, BoardObject boardObjectB) {
        return euclideanDistance(GlobalLevelService.getInstance().
                        getPosition(boardObjectA.getLabel()),
                getPosition(boardObjectB.getLabel())
        );
    }

    /**
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

    public PriorityQueue<Position> getBestGoalWeighingPositionsList() {
        List<Goal> levelGoals = level.getGoals();

        PriorityQueue<Position> goalWeighingPositions = new PriorityQueue<>(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return getLevelObjectRemoteness(o2, levelGoals) - getLevelObjectRemoteness(o1, levelGoals);
            }
        });

        List<BoardObject> levelAgentsAndBoxes = new ArrayList<>();
        levelAgentsAndBoxes.addAll(getLevel().getAgents());
        levelAgentsAndBoxes.addAll(getLevel().getBoxes());

        for (BoardObject boardObject : levelAgentsAndBoxes) {
            Position objectPositon = getPosition(boardObject);
            goalWeighingPositions.add(objectPositon);
        }

        return goalWeighingPositions;
    }

    public int getLevelObjectRemoteness(Position position, List<Goal> goalList) {
        int remoteness = Integer.MAX_VALUE;
        for (Goal goal : goalList) {
            int remotenessToGoal = manhattanDistance(goal.getPosition(), position);
            remoteness = Math.min(remoteness, remotenessToGoal);
        }

        return remoteness;
    }

    public int getLevelObjectRemoteness(BoardObject boardObject, List<Goal> goalList) {
        int remoteness = Integer.MAX_VALUE;
        for (Goal goal : goalList) {
            int remotenessToGoal = manhattanDistance(goal, boardObject);
            remoteness = Math.min(remoteness, remotenessToGoal);
        }

        return remoteness;
    }

    /**
     * Under influence of an agent, this takes a PrimitivePlan
     * and turns it into an ordered list of positions, visited by that agent and its box, without duplicates.
     *
     * @return
     */
    public LinkedList<Position> getOrderedPathWithBox(PrimitivePlan plan, Agent agent) {
        return getPath(plan, getPosition(agent));
    }

    /**
     * Under influence of an agent BDIService, this takes a PrimitivePlan
     * and turns it into an ordered list of positions, visited by that agent and its box, without duplicates.
     *
     * @return
     */
    public LinkedList<Position> getOrderedPathWithBox(PrimitivePlan plan) {
        return getPath(plan, getPosition(BDIService.getInstance().getAgent()));
    }

    public LinkedList<Position> getPath(PrimitivePlan plan, Position agentPosition) {
        LinkedList<Position> bigPath = new LinkedList<>();

        Position previous;
        if (!plan.getActions().isEmpty()
                && plan.getActions().getFirst().getType().equals(ConcreteActionType.PULL)) {
            // if it is a pull action, the agents position is not the first one
            previous = new Position(
                    agentPosition,
                    plan.getActions().getFirst().getAgentDirection().getInverse()
            );
        } else {
            previous = agentPosition;
        }

        bigPath.add(previous);

        for (ConcreteAction action : plan.getActionsClone()) {
            if (!action.getType().equals(ConcreteActionType.NONE)) {
                // the agents next position
                Position next = new Position(previous, action.getAgentDirection());

                if (action instanceof MoveBoxConcreteAction) {
                    // we also need to add the box' position to the path
                    Position nextBox = null;
                    switch (action.getType()) {
                        case PUSH:
                            // if we are pushing, the box should end up in front of the agent
                            bigPath.addLast(new Position(next));
                            nextBox = new Position(next, ((PushConcreteAction) action).getBoxMovingDirection());
                            bigPath.addLast(nextBox);
                            break;
                        case PULL:
                            // if we are pulling, the box should end up in the agents' previous position
                            nextBox = new Position(previous);
                            bigPath.addLast(nextBox);
                            bigPath.addLast(new Position(next));
                            break;
                    }
                } else {
                    bigPath.addLast(new Position(next));
                }

                previous = next;
            }
        }

        LinkedList<Position> path = new LinkedList<>();

        Position previousPosition = bigPath.pollFirst();
        Position nextPosition;
        while ((nextPosition = bigPath.pollFirst()) != null) {
            if (!previousPosition.equals(nextPosition)) {
                path.addLast(previousPosition);
            }
            previousPosition = nextPosition;
        }

        if (!path.isEmpty()) {
            if (!path.getLast().equals(previousPosition)) {
                path.addLast(previousPosition);
            }
        } else {
            path.add(previousPosition);
        }

        return path;
    }

    /**
     * Under influence of an agent BDIService, this takes a PrimitivePlan
     * and turns it into an ordered list of positions, visited by that agent, without duplicates.
     *
     * @param plan
     * @return
     */
    public LinkedList<Position> getOrderedPath(PrimitivePlan plan) {
        LinkedList<Position> path = new LinkedList<>();

        Position previous = getPosition(BDIService.getInstance().getAgent());
        path.add(new Position(previous));

        if (plan != null) {
            LinkedList<ConcreteAction> actionsClone = plan.getActionsClone();

            if (actionsClone != null) {
                for (ConcreteAction action : actionsClone) {
                    // the agents next position
                    if (!action.getType().equals(ConcreteActionType.NONE)) {
                        Position next = new Position(previous, action.getAgentDirection());
                        path.addLast(new Position(next));
                        previous = next;
                    }
                }
            }
        }
        return path;
    }

    /**
     * Merge two paths such that they have a an unbroken traversable connection
     *
     * @param newPath
     * @param originPath
     * @return
     */
    public synchronized LinkedList<Position> mergePaths(LinkedList<Position> newPath,
                                                        LinkedList<Position> originPath) {

        LinkedList<Position> newPathReversed = reversePath(newPath);

        PlanningLevelService pls = new PlanningLevelService(getLevelClone());

        if (originPath.isEmpty()) {
            return newPath;
        }

        if (newPath.isEmpty()) {
            return originPath;
        }

        // Move agent to the last position in its path
        pls.moveAgent(originPath.peekLast());

        // Plan for moving agent from its last position, to newPathReversed's first position
        RGotoAction extendPathAction = new RGotoAction(newPathReversed.peekFirst());

        HTNPlanner htn = new HTNPlanner(pls, extendPathAction, RelaxationMode.NoAgentsNoBoxes);
        PrimitivePlan pseudoPlan = htn.plan();

        // path going from originPath's last position, to newPathReversed's first position
        LinkedList<Position> connectingPath = pls.getOrderedPath(pseudoPlan);
        if (connectingPath.size() > 0) {
            connectingPath.removeFirst();
        }
        if (connectingPath.size() > 0) {
            connectingPath.removeLast();
        }

        // combine the two paths into originPath
        originPath.addAll(connectingPath);
        originPath.addAll(newPathReversed);

        return reversePath(originPath);
    }

    /**
     * Reverse all actions in given path
     *
     * @param path
     * @return
     */
    public synchronized LinkedList<Position> reversePath(LinkedList<Position> path) {
        LinkedList<Position> newPath = new LinkedList<>();

        for (Position position : path) {
            newPath.addFirst(new Position(position));
        }

        return newPath;
    }

    /**
     * Finds an ordered list of obstacles in a path
     *
     * @param path
     * @return
     */
    public LinkedList<Position> getObstaclePositions(LinkedList<Position> path) {
        LinkedList<Position> obstacles = new LinkedList<>();

        Iterator<Position> positions = path.iterator();

        while (positions.hasNext()) {
            Position next = positions.next();
            if (!isFree(next)) {
                if (getCell(next) != BoardCell.AGENT
                        && getCell(next) != BoardCell.AGENT_GOAL
                        && !obstacles.contains(next)) {
                    obstacles.add(next);
                }
            }
        }

        return obstacles;
    }

    /**
     * @return List of the next set of independent goals, 1 from each queue
     */
    public synchronized List<Goal> getIndependentGoals() {
        List<Goal> goals = new ArrayList<>();

        level.getGoalQueues().forEach(goalQueue -> {
            Goal goal = goalQueue.peek();
            if (goal != null) {
                goals.add(goal);
            }
        });

        return goals;
    }

    /**
     * Removes this goal from the queue in which it exists
     *
     * @param goal
     */
    public synchronized void removeGoalFromQueue(Goal goal) {
        level.getGoalQueues().forEach(goalQueue -> {
            if (goalQueue.contains(goal)) {
                // remove this goal from the queue
                goalQueue.remove(goal);
            }
        });
    }

    /**
     * @param position
     * @return A list of free cells adjacent to @position that are not goals
     */
    public synchronized List<Neighbour> getNonGoalFreeNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (isFreeOfGoals(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (isFreeOfGoals(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (isFreeOfGoals(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (isFreeOfGoals(position.getRow() + 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() + 1, position.getColumn()),
                    Direction.SOUTH
            ));
        }

        return neighbours;
    }

    public HashSet<Position> getFreeNeighbourSet(Position position) {
        HashSet<Position> freeNeighbours = new HashSet<>();
        Position n = new Position(position, Direction.NORTH);
        Position s = new Position(position, Direction.SOUTH);
        Position e = new Position(position, Direction.EAST);
        Position w = new Position(position, Direction.WEST);
        if (isFree(n)) freeNeighbours.add(n);
        if (isFree(s)) freeNeighbours.add(s);
        if (isFree(e)) freeNeighbours.add(e);
        if (isFree(w)) freeNeighbours.add(w);
        return freeNeighbours;
    }

    /**
     * If we care about the agents position we must create an obstacle-free-path for the agent.
     *
     * @param path
     * @param agentPosition
     * @param obstaclePosition
     * @param numberOfNeighbours
     * @return
     */
    public synchronized Position getFreeNeighbour(
            final LinkedList<Position> path,
            Position agentPosition,
            Position obstaclePosition,
            int numberOfNeighbours
    ) throws NoFreeNeighboursException {

        // find free path for this obstacle
        LinkedList<Position> obstacleFreePath = getObstacleFreePath(
                path,
                agentPosition,
                obstaclePosition
        );

        return getFreeNeighbour(
                obstacleFreePath,
                obstaclePosition,
                numberOfNeighbours
        );
    }

    /**
     * Returns the closest / deepest free neighbour to the given {@code path} of maximum depth {@code numberOfNeighbours}
     *
     * @param path
     * @param obstaclePosition
     * @param numberOfNeighbours
     * @return
     */
    public synchronized Position getFreeNeighbour(final LinkedList<Position> path,
                                                  Position obstaclePosition,
                                                  int numberOfNeighbours) {


        // as the list is now prioritized, we want to find the first neighbor, at numberOfNeighbours or shallower
        // previously seen positions
        HashSet<Position> previouslySeen = new HashSet<>(path);

        List<Neighbour> neighbours = new ArrayList<>();

        for (Position position : path) {
            if (hasUnseenFreeNeighbour(position, previouslySeen)) {
                Neighbour neighbour = recursiveNeighbour(
                        new Neighbour(position, numberOfNeighbours),
                        previouslySeen,
                        0,
                        numberOfNeighbours
                );

                neighbours.add(neighbour);
            }
        }

        if (neighbours.size() == 0) {
            throw new NoFreeNeighboursException("Could not find any free neighbours.");
        }

        // sum of neighbour depths
        int sumOfDepths = neighbours.stream().mapToInt(Neighbour::getDepth).sum();

        if (sumOfDepths >= numberOfNeighbours) {
            // if there are more or enough neighbours, choose the closest one

            List<Neighbour> neighboursSortedByDistance = neighbours.stream().sorted(new Comparator<Neighbour>() {
                @Override
                public int compare(Neighbour neighbourA, Neighbour neighbourB) {
                    return neighbourA.getPosition().manhattanDist(obstaclePosition)
                            - neighbourB.getPosition().manhattanDist(obstaclePosition);
                }
            }).collect(Collectors.toList());

            return neighboursSortedByDistance.get(0).getPosition();
        }

        // sort the neighbours by depth - highest depth first
        List<Neighbour> neighboursSortedByDepth = neighbours.stream().sorted(new Comparator<Neighbour>() {
            @Override
            public int compare(Neighbour neighbourA, Neighbour neighbourB) {
                return neighbourB.getDepth() - neighbourA.getDepth();
            }
        }).collect(Collectors.toList());

        return neighboursSortedByDepth.get(0).getPosition();
    }

    /**
     * Find 'deepest' neighbour to given {@code position}
     *
     * @return
     */
    private Neighbour recursiveNeighbour(Neighbour neighbour,
                                         HashSet<Position> previouslyDiscovered,
                                         int reachedDepth,
                                         int numberOfNeighbours) {
        if (reachedDepth == numberOfNeighbours) {
            // end recursion if number of neighbours is reached
            return neighbour;
        }

        if (hasUnseenFreeNeighbour(neighbour.getPosition(), previouslyDiscovered)) {
            // recursively find neighbours

            reachedDepth++;

            Neighbour nextNeighbour = new Neighbour(
                    getUnseenFreeNeighbour(neighbour.getPosition(), previouslyDiscovered),
                    reachedDepth
            );


            previouslyDiscovered.add(neighbour.getPosition());
            return recursiveNeighbour(nextNeighbour, previouslyDiscovered, reachedDepth, numberOfNeighbours);
        }

        // return this position if no free neighbours
        return neighbour;
    }

    /**
     * Returns a free neighbour of {@code position}, which does not exist in previouslyDiscovered
     *
     * @param position
     * @param previouslyDiscovered
     * @return
     */
    private Position getUnseenFreeNeighbour(Position position, HashSet<Position> previouslyDiscovered) {
        HashSet<Position> neighbours = getFreeNeighbourSet(position);
        neighbours.removeAll(previouslyDiscovered);
        Iterator it = neighbours.iterator();
        if (it.hasNext()) {
            Position freeeNeighbour = (Position) it.next();
            return new Position(freeeNeighbour);
        } else {
            return null;
        }
    }

    /**
     * Does this {@code position} have free neighbours, which do not exist in {@code previouslyDiscovered}
     *
     * @param position
     * @param previouslyDiscovered
     * @return
     */
    private boolean hasUnseenFreeNeighbour(Position position, HashSet<Position> previouslyDiscovered) {
        HashSet<Position> neighbours = getFreeNeighbourSet(position);
        neighbours.removeAll(previouslyDiscovered);
        return (!neighbours.isEmpty());
    }

    /**
     * Returns the sub-path of {@code path}, excluding other sub-paths blocked by other obstacles than {@code obstacle}
     *
     * @param path
     * @param obstaclePosition
     * @return
     */
    public LinkedList<Position> getObstacleFreePath(LinkedList<Position> path,
                                                    Position agentPosition,
                                                    Position obstaclePosition) {

        LinkedList<Position> subPath = new LinkedList<>();
        LinkedList<Position> ignoredPositions = new LinkedList<>();

        boolean ignoringPositions = false;
        Position ignoreStartPosition = null;

        for (Position position : path) {
            if (!ignoringPositions
                    && !ignoredPositions.contains(position)
                    ) {
                // we potentially want to add these positions
                if (isFree(position)) {
                    // we can add position to sub-path
                    subPath.addLast(position);
                } else if (position.equals(obstaclePosition)) {
                    // this is the obstacle we wish to keep
                    subPath.addLast(position);
                } else if (position.equals(agentPosition)) {
                    // this is the agent we are operating from
                    subPath.addLast(position);
                } else if (getCell(position) == BoardCell.AGENT) {
                    // this is another agent - we ignore it and expect conflict resolution to handle it
                    subPath.addLast(position);
                } else {
                    // we ignore all positions until we find this position again
                    ignoringPositions = true;
                    ignoreStartPosition = position;
                    ignoredPositions.add(position);
                }
            } else {
                if (ignoreStartPosition.equals(position)) {
                    // we are back on the valid sub-path
                    ignoringPositions = false;
                } else {
                    // we do not care about this position
                    ignoredPositions.add(position);
                }
            }
        }
        return subPath;
    }
}