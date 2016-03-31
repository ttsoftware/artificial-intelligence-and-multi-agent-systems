package dtu.agency.board;

import dtu.agency.agent.actions.Direction;
import java.io.Serializable;

public class Position implements Serializable {

    private int row;
    private int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position(Position other) {
        this.row = other.getRow();
        this.column = other.getColumn();
    }

    public Position(Position old, Direction dir) {
        // Coordinates used to interpret direction
        //  R
        //C 0 1 2
        //  1
        //  2
        this.row = old.getRow();
        this.column = old.getColumn();
        switch (dir) { // (0,0) is NORTH-WEST corner
            case NORTH:
                this.row -= 1;
                break;
            case SOUTH:
                this.row += 1;
                break;
            case EAST:
                this.column += 1;
                break;
            case WEST:
                this.column -= 1;
                break;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean equals(Position o) {
        return (this.getColumn() == o.getColumn() && this.getRow() == o.getRow());
    }

    @Override
    public int hashCode() {
        int prime = 619;
        int rowprime = 89; // larger than 70 ;-)
        int result = 1;
        result = prime * result + rowprime * row;
        result = prime * result + column;
        return result;
    }

    public static int manhattanDist(Position p1, Position p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn());
    }

    public int manhattanDist(Position position) {
        return manhattanDist(this, position);
    }

    public static double eucDist(Position p1, Position p2) {
        return Math.sqrt( (p1.getRow() - p2.getRow())^2 + (p1.getColumn() - p2.getColumn())^2 );
    }

    public double eucDist(Position position) {
        return eucDist(this, position);
    }

    public boolean isNeighbour(Position other) {
        return (manhattanDist(other) == 1);
    }

    public Direction getDirectionTo(Position other) { // returns the direction from this to other
        // Coordinates used to extract direction
        //  R
        //C 0 1 2
        //  1
        //  2
        int ns = this.getRow() - other.getRow(); // if positive, other is north of agent
        int ew = this.getColumn() - other.getColumn(); // if positive, other is west of agent
        if (Math.abs(ns) > Math.abs(ew)) {
            if (ns > 0) {
                return Direction.NORTH;
            } else {
                return Direction.SOUTH;
            }
        } else {
            if (ew > 0) {
                return Direction.WEST;
            } else {
                return Direction.EAST;
            }
        }
    }

}
