package dtu.agency.board;

public class Goal extends BoardObject implements Comparable<Goal> {

    private int weight;
    private final Position position;

    public Goal(String label, Position position, int weight) {
        super(label);
        this.position = position;
        this.weight = weight;
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
}
