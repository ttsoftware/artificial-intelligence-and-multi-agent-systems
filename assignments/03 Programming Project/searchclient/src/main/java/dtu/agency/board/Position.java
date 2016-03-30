package dtu.agency.board;

import java.io.Serializable;

public class Position implements Serializable {

    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isAdjacentTo(Position otherPosition) {
        return (Math.abs(otherPosition.getRow() - row) == 1
                && Math.abs(otherPosition.getColumn() - column) == 0)
                || (Math.abs(otherPosition.getRow() - row) == 0
                    && Math.abs(otherPosition.getColumn() - column) == 1);
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() == this.getClass()) {
            Position foreignPosition = (Position) object;
            return (foreignPosition.getRow() == row
                    && foreignPosition.getColumn() == column);
        } else {
            throw new RuntimeException("Invalid position object comparision.");
        }
    }
}
