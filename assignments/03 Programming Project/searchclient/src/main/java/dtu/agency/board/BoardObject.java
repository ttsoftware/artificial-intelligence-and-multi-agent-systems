package dtu.agency.board;

import java.io.Serializable;

public abstract class BoardObject implements Serializable {

    protected final String label;

    public BoardObject(String label) {
        this.label = label;
    }

    public abstract BoardCell getType();

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (object.getClass() != this.getClass())
            return false;
        BoardObject other = (BoardObject) object;
        return (other.getLabel().equals(label));
    }

    public static BoardObject[][] deepCopy(BoardObject[][] other){
        int sizeA = other.length;
        int sizeB = other[0].length;
        BoardObject[][] data = new BoardObject[sizeA][sizeB];
        for (int i=0;i<sizeA;i++) {
            for (int j = 0; j < sizeB; j++) {
                data[i][j] = BoardObject.cloneBoardObject(other[i][j]);
            }
        }
        return data;
    }

    public static BoardObject cloneBoardObject(BoardObject boardObject) {
        if (boardObject == null) return null;

        switch (boardObject.getType()) {

            case FREE_CELL: // handled in first if statement
                return null;

            case WALL:
                Wall wall = (Wall) boardObject;
                return new Wall(wall);

            case BOX:
                Box box = (Box) boardObject;
                return new Box(box);

            case AGENT:
                Agent agent = (Agent) boardObject;
                return new Agent(agent);

            case GOAL:
                Goal goal = (Goal) boardObject;
                return new Goal(goal);

            case AGENT_GOAL:
                AgentAndGoal agentAndGoal = (AgentAndGoal) boardObject;
                return new AgentAndGoal(agentAndGoal);

            case BOX_GOAL:
                BoxAndGoal boxAndGoal = (BoxAndGoal) boardObject;
                return new BoxAndGoal(boxAndGoal);

            default:
                return null;
        }
    }
}
