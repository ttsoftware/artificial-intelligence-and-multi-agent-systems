package dtu.agency.board;

public class Box extends BoardObject {

    private int row;
    private int column;

    public Box(String label, int row, int column) {
        super(label);
        this.column = column;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
