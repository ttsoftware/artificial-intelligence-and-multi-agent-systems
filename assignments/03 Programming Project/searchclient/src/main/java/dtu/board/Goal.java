package dtu.board;

public class Goal extends BoardObject {

    private int weight;
    private final int row;
    private final int column;
    private final String letter;

    public Goal(String letter, int row, int column, int weight) {
        this.letter = letter;
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

    public String getLetter() {
        return letter;
    }
}
