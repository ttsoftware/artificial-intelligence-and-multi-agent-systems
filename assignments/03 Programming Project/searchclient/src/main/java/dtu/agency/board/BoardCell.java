package dtu.agency.board;

public enum BoardCell {
    FREE_CELL,
    WALL,
    BOX,
    AGENT,
    GOAL,
    AGENT_GOAL,
    BOX_GOAL;

    public static BoardCell[][] deepCopy(BoardCell[][] other){
        int sizeA = other.length;
        int sizeB = other[0].length;
        BoardCell[][] data = new BoardCell[sizeA][sizeB];
        for (int i=0;i<sizeA;i++) {
            for (int j = 0; j < sizeB; j++) {
                data[i][j] = other[i][j];
            }
        }
        return data;
    }
}