package dtu.agency.services;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.plans.PrimitivePlan;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LevelService {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

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

    public synchronized boolean move(Agent agent, MoveConcreteAction action) {
        // We must synchronize here to avoid collisions.
        // Do we want to handle conflicts in this step/class?

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
            // TODO: check if this introduces errors, but i think i fixed a bug, else add getInverse() to getBoxMovingDirection()

            if (!moveSuccess) {
                // if we could not move the box, we have to move the agency back
                moveObject(agent, action.getAgentDirection().getInverse());
            }
        }

        return moveSuccess;
    }

    /**
     * Move a single BoardObject into a new position
     * @param boardObject
     * @param direction
     * @return
     */
    protected synchronized boolean moveObject(BoardObject boardObject, Direction direction) {

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
        debug("getObject:");
        return level.getBoardObjects()[pos.getRow()][pos.getColumn()].getLabel();
    }

    public synchronized Position getPosition(BoardObject boardObject) {
        return getPosition(boardObject.getLabel());
    }

    public synchronized Position getPosition(String objectLabel) {
        return level.getBoardObjectPositions().get(objectLabel);
    }

    /**
     * Insert a box into the level
     * Usage: when returning responsibility of the box to this levelservice
     * @param box Box to insert into level
     * @param position Position to insert the box in the level
     */
    protected synchronized void insertBox(Box box, Position position) {
        debug("Inserting box into level",2);
        int row = position.getRow();
        int column = position.getColumn();

        BoardCell[][] boardState = level.getBoardState();
        BoardCell cell = boardState[row][column];

        switch (cell) {       // update the cell where the agent is now located
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
        debug("Box inserted into level.boardState");

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(box.getLabel()) != null)
            throw new AssertionError("Expected the box NOT to exist in the level");
        objectPositions.put(box.getLabel(), new Position(row, column));
        level.setBoardObjectPositions(objectPositions);
        debug("Box inserted into level.boardObjectPositions");

        List<Box> boxes = level.getBoxes();
        if (boxes.contains(box))
            throw new AssertionError("Box should not exist in level before adding it");
        boxes.add(box);
        level.setBoxes(boxes);
        debug("Box inserted into level.boxes");

        BoardObject[][] boardObjects = level.getBoardObjects();
        boardObjects[row][column] = box;
        level.setBoardObjects(boardObjects);
        debug("Box inserted into level.boardObjects",-2);
    }

    /**
     * Insert an agent into the level at a given position
     * Usage: when responsibility of agent is returned to level
     * @param agent Agent to insert into level
     * @param position Position to insert the agent
     */
    protected synchronized void insertAgent(Agent agent, Position position) {
        debug("Inserting Agent into (planning) level",2);
        int row = position.getRow();
        int column = position.getColumn();

        BoardCell[][] boardState = level.getBoardState();
        BoardCell cell = boardState[row][column];

        switch (cell) {       // update the cell where the agent is now located
            case FREE_CELL:
                //level.getBoardState()[row][column] = BoardCell.AGENT;
                boardState[row][column] = BoardCell.AGENT;
                break;
            case GOAL:
                //level.getBoardState()[row][column] = BoardCell.AGENT_GOAL;
                boardState[row][column] = BoardCell.AGENT_GOAL;
                break;
            default:
                throw new AssertionError("Cannot insert agent on any cell but FREE or GOAL cells");
        }
        level.setBoardState(boardState);
        debug("Agent inserted into level.boardState");

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(agent.getLabel()) != null)
            throw new AssertionError("Expected the agent NOT to exist in the level");
        objectPositions.put(agent.getLabel(), new Position(row, column));
        level.setBoardObjectPositions(objectPositions);
        debug("Agent inserted into level.boardObjectPositions");

        List<Agent> agents = level.getAgents();
        if (agents.contains(agent))
            throw new AssertionError("Agent should not exist in level before adding it");
        agents.add(agent);
        level.setAgents(agents);
        debug("Agent inserted into level.agents",-2);

        BoardObject[][] boardObjects = level.getBoardObjects();
        boardObjects[row][column] = agent;
        level.setBoardObjects(boardObjects);
    }

    /**
     * Removing a box from a level
     * Usage: when assuming control and responsibility for that box in the level
     * @param box Box to remove from the level
     */
    protected synchronized void removeBox(Box box) {
        debug("Removing box from (planning) level",2);
        Position boxPos = getPosition(box);
        int row = boxPos.getRow();
        int column = boxPos.getColumn();

        BoardCell cell = level.getBoardState()[row][column];

        switch (cell) {
            case BOX:
                level.getBoardState()[row][column] = BoardCell.FREE_CELL;
                break;
            case BOX_GOAL:
                level.getBoardState()[row][column] = BoardCell.GOAL;
                break;
            default:
                throw new AssertionError("Cannot remove box if not present");
        }
        debug("Box removed from level.BoardState");

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(box.getLabel()) == null)
            throw new AssertionError("Cannot remove non-existing box");
        objectPositions.remove(box.getLabel());
        level.setBoardObjectPositions(objectPositions);
        debug("Box removed from level.boardObjectPositions");

        ArrayList<Box> boxes = new ArrayList<>(level.getBoxes());
        if (!boxes.contains(box))
            throw new AssertionError("Box should exist in level before removing it");
        boxes.remove(box);
        level.setBoxes(boxes);
        debug("Box removed from (planning) level.boxes");

        BoardObject[][] boardObjects = level.getBoardObjects();
        boardObjects[row][column] = null;
        level.setBoardObjects(boardObjects);
        debug("Box removed from level.boardobjects",-2);
    }

    /**
     * Remove an agent from the level
     * Usage: when assuming responsibility of agent from the level
     * @param agent Agent to remove from level
     */
    protected synchronized void removeAgent(Agent agent){
        debug("Removing agent from (planning) level",2);

        Position agentPos = getPosition(agent);
        int row = agentPos.getRow();
        int column = agentPos.getColumn();

        BoardCell cell = level.getBoardState()[row][column];
        switch (cell) {
            case AGENT:
                level.getBoardState()[row][column] = BoardCell.FREE_CELL;
                break;
            case AGENT_GOAL:
                level.getBoardState()[row][column] = BoardCell.GOAL;
                break;
            default:
                throw new AssertionError("Cannot remove agent if not present");
        }
        debug("Agent removed from Level.BoardState");

        ConcurrentHashMap<String, Position> objectPositions = level.getBoardObjectPositions();
        if (objectPositions.get(agent.getLabel()) == null)
            throw new AssertionError("Cannot remove non-existing agent");
        objectPositions.remove(agent.getLabel());
        level.setBoardObjectPositions(objectPositions);
        debug("Agent removed from Level.BoardObjectPositions");

        List<Agent> agents = level.getAgents();
        if (!agents.contains(agent))
            throw new AssertionError("Agent should exist in level before removing it");
        agents.remove(agent);
        level.setAgents(agents);
        debug("Agent removed from Level.Agents");

        BoardObject[][] boardObjects = level.getBoardObjects();
        boardObjects[row][column] = null;
        level.setBoardObjects(boardObjects);
        debug("Agent removed from level.boardobjects",-2);

    }

    /**
     * We use Manhattan distances to define "closeness"
     *GlobalLevelService.getInstance().
     * @param agent
     * @param goal
     * @return The box closest to @agent which solves @goal
     */
    public synchronized Box closestBox(Agent agent, Goal goal) {

        int shortestDistance = Integer.MAX_VALUE;
        Box shortestDistanceBox = null;

        // Find the closest box which solves @goalGlobalLevelService.getInstance().
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
        return euclideanDistance(GlobalLevelService.getInstance().
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
     * Under influence of an agent BDIService, this takes a PrimitivePlan
     * and turns it into an ordered list of positions, visited by that agent, without duplicates.
     * @param pseudoPlan
     * @return
     */
    public LinkedList<Position> getOrderedPath(PrimitivePlan pseudoPlan) {
        debug("Getting ordered path from (pseudo)plan",2);
        LinkedList<Position> path = new LinkedList<>();
        Position previous = getPosition(BDIService.getInstance().getAgent());
        path.add(new Position(previous));

        for (ConcreteAction action : pseudoPlan.getActionList()) {
            Position next = new Position(previous, action.getAgentDirection());
            if (!path.contains(next)) {
                path.addLast(new Position(next));
            }
            previous = next;
        }
        debug("Path discovered: " + path.toString(),-2);
        return path;
    }

    /**
     * Finds an ordered list of obstacles in a path
     * @param pseudoPath
     * @return
     */
    public LinkedList<Position> getObstaclePositions(LinkedList<Position> pseudoPath) {
        debug("Getting positions of obstacles in path",2);
        LinkedList<Position> obstacles = new LinkedList<>();

        Iterator positions = pseudoPath.iterator();
        positions.next(); // the agent itself.. to be ignored as obstacle :-)

        while (positions.hasNext()) {
            Position next = (Position) positions.next();
            if (!isFree(next)) {
                // TODO: this also finds agents...
                obstacles.add(next);
            }
        }

        debug("Obstacles found: " + obstacles.toString(),-2);
        return obstacles;
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
     * @param path is the set of positions the agent must travel
     * @param size is the number of free neighboring locations we must discover
     * @return
     */
    public LinkedList<HashSet<Position>> getFreeNeighbours(Set<Position> path, int size) {
        debug("Getting positions of free positions to put obstacles at",2);
        LinkedList<HashSet<Position>> all  = new LinkedList<>();
        HashSet<Position> previous = new HashSet<>();
        HashSet<Position> current = new HashSet<>(path);
        int neighbours = 0;
        do {
            // initialize next to hold the new positions
            HashSet<Position> next = new HashSet<>();

            // morphological dilation
            for (Position p : current) {
                next.addAll(getFreeNeighbourSet(p));
            }

            // make sure only new positions are kept
            next.removeAll(current);
            next.removeAll(previous);

            // add the new Positions to output list
            all.addLast(new HashSet<>(next));
            neighbours += next.size();

            // update running variables
            previous.addAll(current);
            current = next;
        } while ( neighbours < size );

        String s = "{";
        for (HashSet<Position> layer : all) {
            s += layer.toString() + "\n";
        }
        debug("Free Positions ordered by layers:\n" + s +"}", -2);

        return all;
    }

    public Position getValidNeighbour(LinkedList<Position> fullPath, Position origin, int depth){
        LinkedList<Position> prePath = new LinkedList<>();
        LinkedList<Position> priorityPath = new LinkedList<>();

        // divide the path in before / after target origin
        ListIterator fullPathPositions = fullPath.listIterator();
        boolean after = false;
        while (fullPathPositions.hasNext()) {
            Position next = (Position) fullPathPositions.next();
            if (after) { // only add possible neighbours
                if (isFree(next)) {
                    priorityPath.addLast(next);
                } else {
                    break;
                }
            } else { // before
                prePath.addFirst(next);
            }
            if (next.equals(origin)) {
                after = true;
                priorityPath.addLast(next);
            }
        }
        priorityPath.addAll(prePath); // prioritized Path

        HashSet<Position> previouslyDiscovered = new HashSet<>(priorityPath);

        Iterator path = priorityPath.listIterator();

        int neighbourcount = 0;
        List<Position> validNeighbours = new ArrayList<>();

        ArrayList<Position> newNeighbours = new ArrayList<>();
        while ( path.hasNext() && (neighbourcount < depth) ) {
            Position cell = (Position) path.next();
            Position n;
            while (hasUnseenFreeNeighbour(cell, previouslyDiscovered) && (neighbourcount < depth)) {
                n = getUnseenFreeNeighbour(cell, previouslyDiscovered);
                neighbourcount++;
                newNeighbours.add(n);
                previouslyDiscovered.add(n);

                Position nn = n;
                while (hasUnseenFreeNeighbour(nn, previouslyDiscovered) && (neighbourcount < depth)) {
                    neighbourcount++;
                    nn = getUnseenFreeNeighbour(nn, previouslyDiscovered);
                    newNeighbours.add(nn);
                    previouslyDiscovered.add(nn);
                }
                validNeighbours.add(nn);
            }
        } // enough neighbours found

        //find closer valid neighbour
        Position validNeighbour = validNeighbours.get(0);
        int minDistance = origin.manhattanDist(validNeighbour);
        for (Position p: validNeighbours) {
            if (origin.manhattanDist(p) < minDistance) {
                minDistance = origin.manhattanDist(p);
                validNeighbour = p;
            }
        }

        return validNeighbour;
    }

    private Position getUnseenFreeNeighbour(Position cell, HashSet<Position> previouslyDiscovered) {
        HashSet<Position> neighbours = getFreeNeighbourSet(cell);
        neighbours.removeAll(previouslyDiscovered);
        Iterator it = neighbours.iterator();
        if (it.hasNext()) {
            Position freeeNeighbour = (Position) it.next();
            return new Position(freeeNeighbour);
        } else {
            return null;
        }
    }

    protected boolean hasUnseenFreeNeighbour(Position cell, HashSet<Position> previouslyDiscovered){
        HashSet<Position> neighbours = getFreeNeighbourSet(cell);
        neighbours.removeAll(previouslyDiscovered);
        return (!neighbours.isEmpty());
    }

}
