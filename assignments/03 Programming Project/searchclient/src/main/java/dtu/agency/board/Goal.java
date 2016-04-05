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

    public Position getPosition() {
        return position;
    }

    @Override
    public int compareTo(Goal otherGoal) {
        return weight - otherGoal.getWeight();
    }
}
