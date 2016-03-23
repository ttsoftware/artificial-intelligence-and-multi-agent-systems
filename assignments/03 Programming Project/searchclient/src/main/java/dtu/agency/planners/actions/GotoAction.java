package dtu.agency.planners.actions;

import dtu.agency.board.Box;

public class GotoAction extends AbstractAction {

    private int row;
    private int column;

    public GotoAction(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public GotoAction(Box box) {
        this.row = boardObjectPositions;
        this.column = box.getColumn();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
