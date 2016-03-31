package dtu.agency.board;

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

    public boolean notWall(Position pos) {
        return boardState[pos.getRow()][pos.getColumn()] != BoardCell.WALL ;
    }
}
