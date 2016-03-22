package dtu.agency.board;

import java.io.Serializable;

public class BoardObject implements Serializable {

    protected String label;
    protected Position position;

    public BoardObject(String label, Position position) {
        this.label = label;
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
