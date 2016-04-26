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

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Agent) {
            return super.equals(object);
        }
        return false;
    }
}
