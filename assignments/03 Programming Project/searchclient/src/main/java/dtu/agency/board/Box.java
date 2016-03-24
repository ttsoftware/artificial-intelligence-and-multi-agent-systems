package dtu.agency.board;

public class Box extends BoardObject {

    private boolean isStationary;

    public Box(String label) {
        super(label);
        this.isStationary = false;
    }

    public boolean isStationary() {
        return isStationary;
    }

    public void setStationary(boolean stationary) {
        isStationary = stationary;
    }
}
