package dtu.agency.board;

public class Agent extends BoardObject {

    private String label;

    public Agent(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
