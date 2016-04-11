package dtu.agency.board;

public class Agent extends BoardObject {
    public Agent(String label) {
        super(label);
    }

    public Agent(Agent other) {
        super(other.getLabel());
    }
}
