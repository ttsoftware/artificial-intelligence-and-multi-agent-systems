package dtu.agency.board;

public class Goal extends BoardObject {

    private int weight;
    private final int row;
    private final int column;

    public Goal(String label, int row, int column, int weight) {
        super(label);
        this.column = column;
        this.row = row;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
