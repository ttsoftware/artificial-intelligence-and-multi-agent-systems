package dtu.agency.board;

public enum BoardCell {
    FREE_CELL,
    WALL,
    BOX,
    AGENT,
    GOAL,
    AGENT_GOAL,
    BOX_GOAL;

    public static BoardCell[][] deepCopy(BoardCell[][] other) {
        int sizeA = other.length;
        int sizeB = other[0].length;
        BoardCell[][] data = new BoardCell[sizeA][sizeB];
        for (int i = 0; i < sizeA; i++) {
            System.arraycopy(other[i], 0, data[i], 0, sizeB);
        }
        return data;
    }
}