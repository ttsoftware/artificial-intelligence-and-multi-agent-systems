package dtu.agency.board;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Level implements Serializable {

    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private ConcurrentHashMap<String, Position> boardObjectPositions;
    private PriorityBlockingQueue<Goal> goalQueue;
    private ConcurrentHashMap<String, List<Goal>> boxesGoals;
    private ConcurrentHashMap<String, List<Box>> goalsBoxes;
    private List<Goal> goals;
    private List<Agent> agents;
    private List<Box> boxes;
    private List<Wall> walls;

    public Level(BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 ConcurrentHashMap<String, Position> boardObjectPositions,
                 PriorityBlockingQueue<Goal> goalQueue,
                 ConcurrentHashMap<String, List<Goal>> boxesGoals,
                 ConcurrentHashMap<String, List<Box>> goalsBoxes,
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

    public ConcurrentHashMap<String, Position> getBoardObjectPositions() {
        return boardObjectPositions;
    }

    public void setBoardObjectPositions(ConcurrentHashMap<String, Position> boardObjectPositions) {
        this.boardObjectPositions = boardObjectPositions;
    }

    public PriorityBlockingQueue<Goal> getGoalQueue() {
        return goalQueue;
    }

    public ConcurrentHashMap<String, List<Goal>> getBoxesGoals() {
        return boxesGoals;
    }

    public ConcurrentHashMap<String, List<Box>> getGoalsBoxes() {
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
}
