package dtu.agency.board;

public class Agent extends BoardObject {
    public Agent(String label) {
        super(label);
    }

    @Override
    public BoardCell getType() {
        return BoardCell.AGENT;
    }

    public Agent(Agent other) {
        super(other.getLabel());
    }
}
