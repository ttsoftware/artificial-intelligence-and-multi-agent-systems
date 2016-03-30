package dtu.agency.board;

import java.io.Serializable;

public abstract class BoardObject implements Serializable {

    protected String label;

    public BoardObject(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
