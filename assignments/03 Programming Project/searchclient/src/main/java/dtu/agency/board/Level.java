package dtu.agency.board;

import dtu.agency.agent.actions.Direction;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

public class Level implements Serializable {

    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private Hashtable<String, Position> boardObjectPositions;
    private PriorityQueue<Goal> goalQueue = new PriorityQueue<>(new GoalComparator());
    private List<Goal> goals = new ArrayList<>();
    private List<Agent> agents = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    public Level(BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 Hashtable<String, Position> boardObjectPositions,
                 PriorityQueue<Goal> goalQueue,
                 List<Goal> goals,
                 List<Agent> agents,
                 List<Box> boxes,
                 List<Wall> walls) {
        this.boardState = boardState;
        this.boardObjects = boardObjects;
        this.boardObjectPositions = boardObjectPositions;
        this.goalQueue = goalQueue;
        this.goals = goals;
        this.agents = agents;
        this.boxes = boxes;
        this.walls = walls;
    }

    public boolean isAdjacent(Position positionOne, Position positionTwo) {
        if (positionOne.getRow() == positionTwo.getRow() && Math.abs(positionOne.getColumn() - positionTwo.getColumn()) == 1) {
            return true;
        }
        if (positionOne.getColumn() == positionTwo.getColumn() && Math.abs(positionOne.getRow() - positionTwo.getRow()) == 1) {
            return true;
        }
        return false;
    }

    public List<Pair<BoardObject, Position>> getNeighbours(Position position) {
        List<Pair<BoardObject, Position>> neighbours = new ArrayList<>();

        if (this.isNotWall(this.boardObjects[position.getRow()][position.getColumn()-1])) {
            neighbours.add(new Pair<>(this.boardObjects[position.getRow()][position.getColumn() - 1],
                    new Position(position.getRow(), position.getColumn() - 1)));
        }
        if (this.isNotWall(this.boardObjects[position.getRow()][position.getColumn()+1])) {
            neighbours.add(new Pair<>(this.boardObjects[position.getRow()][position.getColumn() + 1],
                    new Position(position.getRow(), position.getColumn() + 1)));
        }
        if (this.isNotWall(this.boardObjects[position.getRow()-1][position.getColumn()])) {
            neighbours.add(new Pair<>(this.boardObjects[position.getRow() - 1][position.getColumn()],
                    new Position(position.getRow() - 1, position.getColumn())));
        }
        if (this.isNotWall(this.boardObjects[position.getRow()+1][position.getColumn()])) {
            neighbours.add(new Pair<>(this.boardObjects[position.getRow() + 1][position.getColumn()],
                    new Position(position.getRow() + 1, position.getColumn())));
        }

        return neighbours;
    }

    private boolean isNotWall(BoardObject boardObject) {
        if (boardObject.equals(BoardCell.WALL)) {
            return false;
        }
        return true;
    }

    public Direction getDirection(Position positionOne, Position positionTwo) {
        if (positionOne.getRow() == positionTwo.getRow()) {
            if (positionOne.getColumn() > positionTwo.getColumn()) {
                return Direction.EAST;
            }
            else {
                return Direction.WEST;
            }
        }
        else if (positionOne.getColumn() == positionTwo.getColumn()) {
            if (positionOne.getRow() > positionTwo.getRow()) {
                return Direction.SOUTH;
            }
            else {
                return Direction.NORTH;
            }
        }
        return null;
    }

    public BoardCell[][] getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardCell[][] boardState) {
        this.boardState = boardState;
    }

    public BoardObject[][] getBoardObjects() {
        return boardObjects;
    }

    public void setBoardObjects(BoardObject[][] boardObjects) {
        this.boardObjects = boardObjects;
    }

    public Hashtable<String, Position> getBoardObjectPositions() {
        return boardObjectPositions;
    }

    public void setBoardObjectPositions(Hashtable<String, Position> boardObjectPositions) {
        this.boardObjectPositions = boardObjectPositions;
    }

    public PriorityQueue<Goal> getGoalQueue() {
        return goalQueue;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Goal> getGoals() {
        return goals;
    }
}
