package dtu.board;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Level {

    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private PriorityQueue<Goal> goalQueue = new PriorityQueue<>(new GoalComparator());
    private List<Goal> goals = new ArrayList<>();
    private List<Agent> agents = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    public Level(BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 PriorityQueue<Goal> goalQueue,
                 List<Goal> goals,
                 List<Agent> agents,
                 List<Box> boxes,
                 List<Wall> walls) {
        this.boardState = boardState;
        this.boardObjects = boardObjects;
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

    public PriorityQueue<Goal> getGoalQueue() {
        return goalQueue;
    }

    public void setGoalQueue(PriorityQueue<Goal> goalQueue) {
        this.goalQueue = goalQueue;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }
}
