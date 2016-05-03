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

    public String getColor() {
        return label.substring(0, label.length() - 1);
    }

    public int getNumber() {
        return Integer.valueOf(label.substring(label.length() - 1, label.length()));
    }
}
