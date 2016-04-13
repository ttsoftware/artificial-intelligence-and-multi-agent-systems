package dtu.agency.board;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Level implements Cloneable {
    private BoardCell[][] boardState;
    private BoardObject[][] boardObjects;
    private ConcurrentHashMap<String, Position> boardObjectPositions;
    private List<PriorityBlockingQueue<Goal>> goalQueues;
    private ConcurrentHashMap<String, List<Goal>> boxesGoals;
    private ConcurrentHashMap<String, List<Box>> goalsBoxes;
    private List<Goal> goals;
    private List<Agent> agents;
    private List<Box> boxes;
    private List<Wall> walls;

    public Level(BoardCell[][] boardState,
                 BoardObject[][] boardObjects,
                 ConcurrentHashMap<String, Position> boardObjectPositions,
                 List<PriorityBlockingQueue<Goal>> goalQueues,
                 ConcurrentHashMap<String, List<Goal>> boxesGoals,
                 ConcurrentHashMap<String, List<Box>> goalsBoxes,
                 List<Goal> goals,
                 List<Agent> agents,
                 List<Box> boxes,
                 List<Wall> walls) {
        this.boardState = boardState;
        this.boardObjects = boardObjects;
        this.boardObjectPositions = boardObjectPositions;
        this.goalQueues = goalQueues;
        this.boxesGoals = boxesGoals;
        this.goalsBoxes = goalsBoxes;
        this.goals = goals;
        this.agents = agents;
        this.boxes = boxes;
        this.walls = walls;
    }

    public Level(Level level) {
        this.boardState = level.getBoardState();
        this.boardObjects = level.getBoardObjects();
        this.boardObjectPositions = level.getBoardObjectPositions();
        this.goalQueues = level.getGoalQueues();
        this.boxesGoals = level.getBoxesGoals();
        this.goalsBoxes = level.getGoalsBoxes();
        this.goals = level.getGoals();
        this.agents = level.getAgents();
        this.boxes = level.getBoxes();
        this.walls = level.getWalls();
    }

//    public Level(Level other) {
//        int rows = other.getBoardObjects().length;
//        int columns = other.getBoardObjects()[0].length;
//
//        this.boardState = new BoardCell[rows][columns];
//
//        System.arraycopy(other.getBoardState(), 0, this.boardState, 0, rows*columns);
//
//        for (int i = rows; i > 0; --i) {
//            for (int j = columns; j > 0; --j) {
//                this.boardState[i][j] = other.getBoardState()[i][j];
//            }
//        }
//
//        this.boardObjects = new BoardObject[rows][columns];
//        for (int i = rows; i > 0; --i) {
//            for (int j = columns; j > 0; --j) {
//                BoardObject obj = other.getBoardObjects()[i][j];
//                if (obj != null) {
//                    switch (obj.getType()){
//                        case FREE_CELL:
//                            this.boardObjects[i][j] = null;
//                            break;
//                        case WALL:
//                            this.boardObjects[i][j] = new Wall((Wall) obj);
//                            break;
//                        case BOX:
//                            this.boardObjects[i][j] = new Box((Box) obj);
//                            break;
//                        case AGENT:
//                            this.boardObjects[i][j] = new Agent((Agent) obj);
//                            break;
//                        case GOAL:
//                            this.boardObjects[i][j] = new Goal((Goal) obj);
//                            break;
//                        case SOLVED_GOAL:
//                            this.boardObjects[i][j] = new CoveredGoal((CoveredGoal) obj);
//                            break;
//                        case AGENT_GOAL:
//                            this.boardObjects[i][j] = new AgentAndGoal((AgentAndGoal) obj);
//                            break;
//                        case BOX_GOAL:
//                            this.boardObjects[i][j] = new BoxAndGoal((BoxAndGoal) obj);
//                            break;
//                    }
//                } else {
//                    this.boardObjects[i][j] = null;
//                }
//            }
//        }
//
//        this.boardObjectPositions = new ConcurrentHashMap<>(other.getBoardObjectPositions());
//        this.goalQueues = new ArrayList<>(other.getGoalQueues());
//        this.boxesGoals = new ConcurrentHashMap<>(other.getBoxesGoals());
//        this.goalsBoxes = new ConcurrentHashMap<>(other.getGoalsBoxes());
//        this.goals  = new ArrayList<>(other.getGoals());
//        this.agents = new ArrayList<>(other.getAgents());
//        this.boxes  = new ArrayList<>(other.getBoxes());
//        this.walls  = new ArrayList<>(other.getWalls());
//    }

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

    public List<PriorityBlockingQueue<Goal>> getGoalQueues() {
        return goalQueues;
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

    @Override
    public Level clone() {
        Level level = null;
        try {
            level = (Level) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return level;
    }
}
