package dtu.agency.services;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.board.*;
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

        // update the cell where the agent is now located
        switch (cell) {
            case FREE_CELL:
                boardState[row][column] = BoardCell.BOX;
                break;
            case GOAL:
                boardState[row][column] = BoardCell.BOX_GOAL;
                break;
            default:
                throw new AssertionError("Cannot insert box on any cell but FREE or GOAL cells");
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

        switch (cell) {       // update the cell where the agent is now located
            case FREE_CELL:
                boardState[row][column] = BoardCell.AGENT;
                break;
            case GOAL:
                boardState[row][column] = BoardCell.AGENT_GOAL;
                break;
            default:
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
                System.err.println(sa + "lvl: agents: " + level.getAgents() + " boxes: " + level.getBoxes());
                System.err.println(sa + "lvl: objectPositions: " + level.getBoardObjectPositions());
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

    public Position getBestGoalWeighingPosition() {
        List<Goal> levelGoals = level.getGoals();

        List<BoardObject> levelAgentsAndBoxes = new ArrayList<>();
        levelAgentsAndBoxes.addAll(getLevel().getAgents());
        levelAgentsAndBoxes.addAll(getLevel().getBoxes());

        int minRemoteness = Integer.MAX_VALUE;
        BoardObject mostRemoteObject = null;

        for (BoardObject boardObject : levelAgentsAndBoxes) {
            int objectRemoteness = getLevelObjectRemoteness(boardObject, levelGoals);
            if (minRemoteness > objectRemoteness) {
                minRemoteness = objectRemoteness;
                mostRemoteObject = boardObject;
            }
        }

        return getPosition(mostRemoteObject.getLabel());
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
     * Under influence of an agent BDIService, this takes a PrimitivePlan
     * and turns it into an ordered list of positions, visited by that agent and its box, without duplicates.
     *
     * @return
     */
    public LinkedList<Position> getOrderedPathWithBox(PrimitivePlan plan) {
        LinkedList<Position> bigPath = new LinkedList<>();

        Position previous = getPosition(BDIService.getInstance().getAgent());
        bigPath.add(new Position(previous));

        for (ConcreteAction action : plan.getActionsClone()) {
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

        LinkedList<Position> path = new LinkedList<>();

        Position previousPosition = bigPath.pollFirst();
        Position nextPosition;
        while ((nextPosition = bigPath.pollFirst()) != null) {
            if (!previousPosition.equals(nextPosition)) {
                path.addLast(previousPosition);
            }
            previousPosition = nextPosition;
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

        for (ConcreteAction action : plan.getActionsClone()) {
            // the agents next position
            Position next = new Position(previous, action.getAgentDirection());
            path.addLast(new Position(next));
            previous = next;
        }
        return path;
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
        Position agentPosition = positions.next();

        while (positions.hasNext()) {
            Position next = positions.next();
            if (!isFree(next)) {
                // TODO: This should also finds agents. Maybe. Who knows?
                if (!next.equals(agentPosition) && !obstacles.contains(next)) {
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
            Goal goal = goalQueue.poll();
            if (goal != null) {
                goals.add(goal);
            }
        });

        return goals;
    }

    /**
     * @param position
     * @return A list of adjacent cells containing a box or an agent
     */
    public synchronized List<Neighbour> getMoveableNeighbours(Position position) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (isMoveable(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (isMoveable(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (isMoveable(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (isMoveable(position.getRow() + 1, position.getColumn())) {
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

        if (isFree(position.getRow(), position.getColumn() - 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (isFree(position.getRow(), position.getColumn() + 1)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (isFree(position.getRow() - 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (isFree(position.getRow() + 1, position.getColumn())) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() + 1, position.getColumn()),
                    Direction.SOUTH
            ));
        }

        return neighbours;
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

    /**
     * @param position
     * @param objectsToIgnore
     * @return A list of free cells adjacent to @position, and the neighbours that contains one of @objectsToIgnore,
     * if any of them exists
     */
    public synchronized List<Neighbour> getFreeNeighbours(Position position, List<BoardObject> objectsToIgnore) {
        List<Neighbour> neighbours = new ArrayList<>();

        if (isFree(position.getRow(), position.getColumn() - 1, objectsToIgnore)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() - 1),
                    Direction.WEST
            ));
        }
        if (isFree(position.getRow(), position.getColumn() + 1, objectsToIgnore)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow(), position.getColumn() + 1),
                    Direction.EAST
            ));
        }
        if (isFree(position.getRow() - 1, position.getColumn(), objectsToIgnore)) {
            neighbours.add(new Neighbour(
                    new Position(position.getRow() - 1, position.getColumn()),
                    Direction.NORTH
            ));
        }
        if (isFree(position.getRow() + 1, position.getColumn(), objectsToIgnore)) {
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
     * Finding a list of sets of unique positions, by dilating the path given
     * until enough free positions is found to absorb *size* obstacles.
     *
     * @param path is the set of positions the agent must travel
     * @param size is the number of free neighboring locations we must discover (max 2)
     * @return
     */
    public List<ParkingSpace> getParkingSpaces(LinkedList<Position> path, int size) {
        List<ParkingSpace> parkingSpaces = new ArrayList<>();

        for(int i = path.size() - 1; i >= 0 && size != 0; i--)
        {
            List<Neighbour> neighbours = getFreeNeighbours(path.get(i));

            if(neighbours.size() > 0) {
                parkingSpaces.add(new ParkingSpace(neighbours.get(0).getPosition(), i));
                size--;
            }
        }

        return parkingSpaces;
    }

    /**
     * Returns a SortedSet of free neighbouring positions, ordered by their distance to the path.
     * These free positions may be adjacent to the path, or adjacent to free positions adjacent to the path and so on...
     * <p>
     * This function is roughly O(N), where N is the set of all cells in the level
     *
     * @param path
     * @param numberOfNeighbours
     * @return
     */
    public synchronized Position getFreeNeighbour(final LinkedList<Position> path, Position agentPosition, Position obstaclePosition, int numberOfNeighbours) {

        // find free path for this obstacle
        LinkedList<Position> obstacleFreePath = getObstacleFreePath(
                path,
                agentPosition,
                obstaclePosition
        );

        // find weighted sub path
        PriorityQueue<Position> weightSubPath = weightedObstacleSubPath(obstacleFreePath, obstaclePosition);

        // as the list is now prioritized, we want to find the first neighbor, at numberOfNeighbours or shallower
        // previously seen positions
        HashSet<Position> previouslySeen = new HashSet<>(obstacleFreePath);

        Position weightedPosition;
        // iterate in weighted order
        while ((weightedPosition = weightSubPath.poll()) != null) {
            if (hasUnseenFreeNeighbour(weightedPosition, previouslySeen)) {
                return recursiveNeighbour(weightedPosition, previouslySeen, numberOfNeighbours);
            }
        }

        throw new RuntimeException("We cannot find a free neighbour for this obstacle");
    }

    /**
     * Find 'deepest' neighbour to given {@code position}
     *
     * @return
     */
    private Position recursiveNeighbour(Position position,
                                        HashSet<Position> previouslyDiscovered,
                                        int numberOfNeighbours) {
        if (numberOfNeighbours == 0) {
            // end recursion if number of neighbours is reached
            return position;
        }

        if (hasUnseenFreeNeighbour(position, previouslyDiscovered)) {
            // recursively find neighbours
            Position neighbour = getUnseenFreeNeighbour(position, previouslyDiscovered);
            previouslyDiscovered.add(position);
            return recursiveNeighbour(neighbour, previouslyDiscovered, --numberOfNeighbours);
        }

        // return this position if no free neighbours
        return position;
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

        boolean ignoringPositions = false;
        Position ignoreStartPosition = null;

        for (Position position : path) {
            if (!ignoringPositions) {
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
                } else {
                    // we ignore all positions until we find this position again
                    ignoringPositions = true;
                    ignoreStartPosition = position;
                }
            } else {
                // we potentially want to ignore these positions
                if (ignoreStartPosition.equals(position)) {
                    // we are back on the valid sub-path
                    ignoringPositions = false;
                } else {
                    // we do not care about this position
                }
            }
        }
        return subPath;
    }

    /**
     * Returns the weighted {@code subPath} of given {@code obstaclePosition}.
     * Positions in {@code subPath} are weighted by distance from {@code obstaclePosition}
     *
     * @param subPath
     * @param obstaclePosition
     * @return
     */
    public PriorityQueue<Position> weightedObstacleSubPath(LinkedList<Position> subPath,
                                                           Position obstaclePosition) {
        PriorityQueue<Position> weightedSubPath = new PriorityQueue<>(new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                // Order by distance form obstacle
                return a.manhattanDist(obstaclePosition) - b.manhattanDist(obstaclePosition);
            }
        });

        weightedSubPath.addAll(subPath);

        return weightedSubPath;
    }
}