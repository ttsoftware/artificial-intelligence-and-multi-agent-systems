package dtu.agency.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Level {

    private int rowCount;
    private int columnCount;

    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private ConcurrentHashMap<String, Position> boardObjectPositions;
    private List<PriorityBlockingQueue<Goal>> goalQueues;
    private ConcurrentHashMap<String, List<Goal>> boxesGoals;
    private ConcurrentHashMap<String, List<Box>> goalsBoxes;

    private ConcurrentHashMap<String, Agent> agentsMap;
    private ConcurrentHashMap<String, Box> boxesMap;
    private ConcurrentHashMap<String, Goal> goalsMap;

    private List<Goal> goals;
    private List<Agent> agents;
    private List<Box> boxes;
    private List<Wall> walls;

    public Level(Level level) {
        this.rowCount = level.getRowCount();
        this.columnCount = level.getColumnCount();
        this.boardState = BoardCell.deepCopy(level.getBoardState());
        this.boardObjects = BoardObject.deepCopy(level.getBoardObjects());
        this.boardObjectPositions = new ConcurrentHashMap<>(level.getBoardObjectPositions());
        this.goalQueues = new ArrayList<>(level.getGoalQueues());
        this.boxesGoals = new ConcurrentHashMap<>(level.getBoxesGoals());
        this.goalsBoxes = new ConcurrentHashMap<>(level.getGoalsBoxes());
        this.agentsMap = new ConcurrentHashMap<>();
        this.boxesMap = new ConcurrentHashMap<>();
        this.goalsMap = new ConcurrentHashMap<>();
        this.goals = new ArrayList<>(level.getGoals());
        this.agents = new ArrayList<>(level.getAgents());
        this.boxes = new ArrayList<>(level.getBoxes());
        this.walls = new ArrayList<>(level.getWalls());
    }

    public Level(int rowCount,
                 int columnCount,
                 BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 ConcurrentHashMap<String, Position> boardObjectPositions,
                 List<PriorityBlockingQueue<Goal>> goalQueues,
                 ConcurrentHashMap<String, List<Goal>> boxesGoals,
                 ConcurrentHashMap<String, List<Box>> goalsBoxes,
                 ConcurrentHashMap<String, Agent> agentsMap,
                 ConcurrentHashMap<String, Box> boxesMap,
                 ConcurrentHashMap<String, Goal> goalsMap,
                 List<Goal> goals,
                 List<Agent> agents,
                 List<Box> boxes,
                 List<Wall> walls) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.boardState = boardState;
        this.boardObjects = boardObjects;
        this.boardObjectPositions = boardObjectPositions;
        this.goalQueues = goalQueues;
        this.boxesGoals = boxesGoals;
        this.goalsBoxes = goalsBoxes;
        this.agentsMap = agentsMap;
        this.boxesMap = boxesMap;
        this.goalsMap = goalsMap;
        this.goals = goals;
        this.agents = agents;
        this.boxes = boxes;
        this.walls = walls;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public BoardCell[][] getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardCell[][] boardState) {
        this.boardState = boardState;
    }

    public BoardObject[][] getBoardObjects() {
        return boardObjects.clone();
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

    public List<PriorityBlockingQueue<Goal>> getGoalQueues() {
        return goalQueues;
    }

    public void setGoalQueues(List<PriorityBlockingQueue<Goal>> goalQueues) {
        this.goalQueues = goalQueues;
    }

    public ConcurrentHashMap<String, List<Goal>> getBoxesGoals() {
        return boxesGoals;
    }

    public ConcurrentHashMap<String, List<Box>> getGoalsBoxes() {
        return goalsBoxes;
    }

    public ConcurrentHashMap<String, Agent> getAgentsMap() {
        return agentsMap;
    }

    public ConcurrentHashMap<String, Box> getBoxesMap() {
        return boxesMap;
    }

    public ConcurrentHashMap<String, Goal> getGoalsMap() {
        return goalsMap;
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

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    @Override
    public String toString() {
        String returnString = "";
        for (int row = 0; row < boardState.length; row++) {
            for (int cell = 0; cell < boardState[row].length; cell++) {
                BoardCell cellType = boardState[row][cell];
                switch (cellType) {
                    case FREE_CELL:
                        returnString += boardObjects[row][cell].getLabel();
                        break;
                    case WALL:
                        returnString += boardObjects[row][cell].getLabel().substring(0, 1);
                        break;
                    case BOX:
                        String boxLabel = boardObjects[row][cell].getLabel();
                        boxLabel = boxLabel.substring(boxLabel.length() - 4, boxLabel.length() - 3);
                        returnString += boxLabel;
                        break;
                    case AGENT:
                        String agentLabel = boardObjects[row][cell].getLabel();
                        agentLabel = agentLabel.substring(agentLabel.length() - 1, agentLabel.length());
                        returnString += agentLabel;
                        break;
                    case GOAL:
                        String goalLabel = boardObjects[row][cell].getLabel().substring(0, 1);
                        returnString += goalLabel;
                        break;
                    case AGENT_GOAL:
                        returnString += boardObjects[row][cell].getLabel();
                        break;
                    case BOX_GOAL:
                        returnString += boardObjects[row][cell].getLabel();
                        break;
                }
            }
            returnString += "\n";
        }
        return returnString;
    }
}
