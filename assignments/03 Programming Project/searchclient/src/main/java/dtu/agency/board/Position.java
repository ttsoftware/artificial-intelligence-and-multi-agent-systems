package dtu.agency.board;

import java.io.Serializable;

public class Position implements Serializable {

    private int row;
    private int column;

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

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean equals(Position o) {
        return (this.getColumn() == o.getColumn() && this.getRow() == o.getRow());
    }

    @Override
    public int hashCode() {
        int prime = 619;
        int rowprime = 89;
        int result = 1;
        result = prime * result + rowprime * row;
        result = prime * result + column;
        return result;
    }

    public static int manhattanDist(Position p1, Position p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getColumn() - p2.getColumn())
    }

    public static double eucDist(Position p1, Position p2) {
        return Math.sqrt( (p1.getRow() - p2.getRow())^2 + (p1.getColumn() - p2.getColumn())^2 )
    }

    public int manhattanDist(Position position) {
        return manhattanDist(this, Position position);
}
