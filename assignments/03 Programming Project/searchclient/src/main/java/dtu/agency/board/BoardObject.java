package dtu.agency.board;

import java.io.Serializable;

public abstract class BoardObject implements Serializable {

    protected final String label;

    public BoardObject(String label) {
        this.label = label;
    }

    public abstract BoardCell getType();

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (object.getClass() == this.getClass()) {
            BoardObject other = (BoardObject) object;
            return (other.getLabel().equals(label));
        } else {
            throw new RuntimeException("Invalid position object comparision.");
        }
    }
}
