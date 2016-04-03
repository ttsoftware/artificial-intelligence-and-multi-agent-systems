package dtu.agency.board;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

public class Level implements Serializable {

    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private Hashtable<String, Position> boardObjectPositions;
    private PriorityQueue<Goal> goalQueue;
    private Hashtable<String, List<Goal>> boxesGoals;
    private Hashtable<String, List<Box>> goalsBoxes;
    private List<Goal> goals;
    private List<Agent> agents;
    private List<Box> boxes;
    private List<Wall> walls;

    public Level(BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 Hashtable<String, Position> boardObjectPositions,
                 PriorityQueue<Goal> goalQueue,
                 Hashtable<String, List<Goal>> boxesGoals,
                 Hashtable<String, List<Box>> goalsBoxes,
                 List<Goal> goals,
                 List<Agent> agents,
                 List<Box> boxes,
                 List<Wall> walls) {
        this.boardState = boardState;
        this.boardObjects = boardObjects;
        this.boardObjectPositions = boardObjectPositions;
        this.goalQueue = goalQueue;
        this.boxesGoals = boxesGoals;
        this.goalsBoxes = goalsBoxes;
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

    public Hashtable<String, List<Goal>> getBoxesGoals() {
        return boxesGoals;
    }

    public Hashtable<String, List<Box>> getGoalsBoxes() {
        return goalsBoxes;
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
        //System.err.println(boardState[pos.getRow()][pos.getColumn()]);
        return boardState[pos.getRow()][pos.getColumn()] != BoardCell.WALL ;
    }
}
