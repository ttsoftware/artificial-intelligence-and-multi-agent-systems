package dtu.agency.board;

import dtu.agency.actions.concreteaction.Direction;

import java.io.Serializable;
import java.util.LinkedList;

public class Position implements Serializable {

    private final int row;
    private final int column;

    public Position(Position other) {
        this.row = other.getRow();
        this.column = other.getColumn();
    }

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position(Position old, Direction dir) {
        // Coordinates used to interpret direction
        //  R
        //C 0 1 2
        //  1
        //  2
        switch (dir) { // (0,0) is NORTH-WEST corner
            case NORTH:
                this.row = old.getRow() - 1;
                this.column = old.getColumn();
                break;
            case SOUTH:
                this.row = old.getRow() + 1;
                this.column = old.getColumn();
                break;
            case EAST:
                this.row = old.getRow();
                this.column = old.getColumn() + 1;
                break;
            case WEST:
                this.row = old.getRow();
                this.column = old.getColumn() - 1;
                break;
            default:
                this.row = old.getRow();
                this.column = old.getColumn();
                break;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static int manhattanDist(Position p1, Position p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

    public int manhattanDist(Position position) {
        return manhattanDist(this, position);
    }

    public boolean isAdjacentTo(Position otherPosition) {
        return (Math.abs(otherPosition.getRow() - row) == 1
                && Math.abs(otherPosition.getColumn() - column) == 0)
                || (Math.abs(otherPosition.getRow() - row) == 0
                    && Math.abs(otherPosition.getColumn() - column) == 1);
    }

    /**
     * The shortest distance from this to any position in path
     * @param path
     * @return
     */
    public int distanceFromPath(LinkedList<Position> path) {
        if (path.contains(this)) {
            // this is in the path
            return 0;
        }
        // find the smallest manhattan distance
        return path.stream().mapToInt(position -> position.manhattanDist(this)).min().getAsInt();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (object instanceof Position) {
            return (((Position) object).getRow() == row
                    && ((Position) object).getColumn() == column);
        }
        throw new RuntimeException("Invalid position object comparison.");
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
        return "(" + Integer.toString(getRow()) + "," + Integer.toString(getColumn()) + ")";
    }
}
