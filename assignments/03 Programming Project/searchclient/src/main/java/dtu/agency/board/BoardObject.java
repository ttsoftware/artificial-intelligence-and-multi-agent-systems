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

    public static BoardObject[][] deepCopy(BoardObject[][] other) {
        int sizeA = other.length;
        int sizeB = other[0].length;
        BoardObject[][] data = new BoardObject[sizeA][sizeB];
        for (int i = 0; i < sizeA; i++) {
            for (int j = 0; j < sizeB; j++) {
                data[i][j] = BoardObject.cloneBoardObject(other[i][j]);
            }
        }
        return data;
    }

    private static BoardObject cloneBoardObject(BoardObject boardObject) {
        switch (boardObject.getType()) {
            case FREE_CELL:
                return new Empty((Empty) boardObject);
            case WALL:
                return new Wall((Wall) boardObject);
            case BOX:
                return new Box((Box) boardObject);
            case AGENT:
                return new Agent((Agent) boardObject);
            case GOAL:
                return new Goal((Goal) boardObject);
            case AGENT_GOAL:
                return new AgentAndGoal((AgentAndGoal) boardObject);
            case BOX_GOAL:
                return new BoxAndGoal((BoxAndGoal) boardObject);
            default:
                throw new RuntimeException("Invalid boardObject");
        }
    }
}
