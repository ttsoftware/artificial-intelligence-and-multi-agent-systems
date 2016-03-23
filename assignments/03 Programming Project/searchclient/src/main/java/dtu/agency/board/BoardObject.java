package dtu.agency.board;

import java.io.Serializable;

public class BoardObject implements Serializable {

    protected String label;

    public BoardObject(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
