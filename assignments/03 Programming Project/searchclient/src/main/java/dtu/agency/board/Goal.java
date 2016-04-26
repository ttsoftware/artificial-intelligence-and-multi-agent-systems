package dtu.agency.board;

public class Goal extends BoardObject implements Comparable<Goal> {

    private int weight;
    private final Position position;

    public Goal(String label, Position position, int weight) {
        super(label);
        this.position = position;
        this.weight = weight;
    }

    public Goal(Goal other) {
        super(other.getLabel());
        this.position = new Position(other.getPosition());
        this.weight = other.getWeight();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) { this.weight = weight; }

    public Position getPosition() {
        return position;
    }

    public boolean equals(Goal otherGoal) { return this.position.equals(otherGoal.position) && this.label.equals(otherGoal.label); }

    @Override
    public int compareTo(Goal otherGoal) {
        return weight - otherGoal.getWeight();
    }

    @Override
    public BoardCell getType() {
        return BoardCell.GOAL;
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Goal) {
            return super.equals(object);
        }
        return false;
    }
}
