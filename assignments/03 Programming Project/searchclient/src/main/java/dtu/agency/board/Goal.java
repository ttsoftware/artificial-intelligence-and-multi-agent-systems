package dtu.agency.board;

public class Goal extends BoardObject implements Comparable<Goal> {

    private int weight;
    private final Position goalPosition; // new FINAL variable

    public Goal(String label, int row, int column, int weight) {
        super(label, new Position(row, column));
        this.goalPosition = new Position(row, column);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public Position getPosition() {
        return this.goalPosition;
    }

    public int getRow() {
        return goalPosition.getRow();
    }

    public int getColumn() {
        return goalPosition.getColumn();
    }

    @Override
    public int compareTo(Goal otherGoal) {
        return weight - otherGoal.getWeight();
    }
}
