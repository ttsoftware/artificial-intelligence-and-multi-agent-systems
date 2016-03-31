package dtu.agency.board;

public class Goal extends BoardObject {

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
}
