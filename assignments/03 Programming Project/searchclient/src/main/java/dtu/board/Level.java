package dtu.board;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Level {

    private BoardCell[][] BoardState;
    private BoardObject[][] BoardObjects;
    private PriorityQueue<Goal> goals = new PriorityQueue<>(new GoalComparator());
    private List<Agent> agents = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    private List<Box> walls = new ArrayList<>();

    public BoardCell[][] getBoardState() {
        return BoardState;
    }

    public void setBoardState(BoardCell[][] boardState) {
        BoardState = boardState;
    }

    public BoardObject[][] getBoardObjects() {
        return BoardObjects;
    }

    public void setBoardObjects(BoardObject[][] boardObjects) {
        BoardObjects = boardObjects;
    }

    public PriorityQueue<Goal> getGoals() {
        return goals;
    }

    public void setGoals(PriorityQueue<Goal> goals) {
        this.goals = goals;
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

    public List<Box> getWalls() {
        return walls;
    }

    public void setWalls(List<Box> walls) {
        this.walls = walls;
    }
}
