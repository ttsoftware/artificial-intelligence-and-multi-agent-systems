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

    @Override
    public int hashCode() {
        int prime = 619;
        //int rowprime = 89; // larger than 70 ;-)
        int result = 1;
        //result = prime * result + rowprime * row;
        result = prime * result + row;
        result = prime * result + column;
        return result;
    }

    public String toString() {
        String s = "(" +
                Integer.toString(getRow()) +
                "," +
                Integer.toString(getColumn()) +
                ")";
        return s;
    }
}
