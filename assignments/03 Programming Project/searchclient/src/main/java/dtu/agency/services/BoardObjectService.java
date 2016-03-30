package dtu.agency.services;

import dtu.agency.agent.actions.Direction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BoardObjectService {

    private BoardObject[][] boardObjects;
    private BoardCell[][] boardState;

    public BoardObjectService(BoardObject[][] boardObjects, BoardCell[][] boardState) {
        this.boardObjects = boardObjects;
        this.boardState = boardState;
    }

    public boolean isAdjacent(Position positionOne, Position positionTwo) {
        if (positionOne.getRow() == positionTwo.getRow() && Math.abs(positionOne.getColumn() - positionTwo.getColumn()) == 1) {
            return true;
        }
        if (positionOne.getColumn() == positionTwo.getColumn() && Math.abs(positionOne.getRow() - positionTwo.getRow()) == 1) {
            return true;
        }
        return false;
    }

//    public List<Pair<BoardObject, Position>> getFreeNeighbours(Position position) {
//        List<Pair<BoardObject, Position>> neighbours = new ArrayList<>();
//
//        neighbours.add(isFreeNeighbour(position.getRow(), position.getColumn()-1));
//        neighbours.add(isFreeNeighbour(position.getRow(), position.getColumn()+1));
//        neighbours.add(isFreeNeighbour(position.getRow()-1, position.getColumn()));
//        neighbours.add(isFreeNeighbour(position.getRow()+1, position.getColumn()));
//
//        return neighbours;
//    }

//    private Pair<BoardObject, Position> isFreeNeighbour(int row, int column) {
//        if (isNotWallAt(row, column) && !isStationaryBox(row, column)) {
//            return new Pair<>(boardObjects[row][column], new Position(row, column));
//        }
//        return null;
//    }

    public List<Pair<Position, Direction>> getFreeNeighbours(Position position) {
        List<Pair<Position, Direction>> neighbours = new ArrayList<>();

        if (isFreeNeighbour(position.getRow(), position.getColumn()-1)) {
            neighbours.add(new Pair<>(new Position(position.getRow(), position.getColumn()-1), Direction.EAST));
        }
        if (isFreeNeighbour(position.getRow(), position.getColumn()+1)) {
            neighbours.add(new Pair<>(new Position(position.getRow(), position.getColumn()+1), Direction.WEST));
        }
        if (isFreeNeighbour(position.getRow()-1, position.getColumn())) {
            neighbours.add(new Pair<>(new Position(position.getRow()-1, position.getColumn()), Direction.NORTH));
        }
        if (isFreeNeighbour(position.getRow()+1, position.getColumn())) {
            neighbours.add(new Pair<>(new Position(position.getRow()+1, position.getColumn()), Direction.SOUTH));
        }

        return neighbours;
    }

//    public Position getNeighbourInDirection(Position position, Direction direction) {
//        Position neighbourPosition;
//
//        switch (direction) {
//            case NORTH:
//                neighbourPosition = new Position(position.getRow()-1, position.getColumn());
//                break;
//            case SOUTH:
//                neighbourPosition = new Position(position.getRow()+1, position.getColumn());
//                break;
//            case WEST:
//                neighbourPosition = new Position(position.getRow(), position.getColumn()-1);
//                break;
//            case EAST:
//                neighbourPosition = new Position(position.getRow(), position.getColumn()+1);
//                break;
//            default:
//                return null;
//        }
//
//        return neighbourPosition;
//    }

    private boolean isFreeNeighbour(int row, int column) {
        if (row >= 0 && column >= 0 && boardState.length > row && boardState[0].length > column) {
            if (boardState[row][column].equals(BoardCell.FREE_CELL)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStationaryBox(int row, int column) {
        BoardObject object = boardObjects[row][column];
        if (boardState[row][column].equals(BoardCell.BOX) || boardState[row][column].equals(BoardCell.BOX_GOAL)) {
            if (((Box)object).isStationary()) {
                return true;
            }
        }
        return false;
    }

    public Direction getMovingDirection(Position positionOne, Position positionTwo) {
        if (positionOne.getRow() == positionTwo.getRow()) {
            if (positionOne.getColumn() > positionTwo.getColumn()) {
                return Direction.EAST;
            }
            else {
                return Direction.WEST;
            }
        }
        else if (positionOne.getColumn() == positionTwo.getColumn()) {
            if (positionOne.getRow() > positionTwo.getRow()) {
                return Direction.SOUTH;
            }
            else {
                return Direction.NORTH;
            }
        }
        return null;
    }

    public Position getPositionInDirection(Position currentPosition, Direction movingDirection) {
        switch (movingDirection) {
            case NORTH:
                return new Position(currentPosition.getRow()-1, currentPosition.getColumn());
            case SOUTH:
                return new Position(currentPosition.getRow()+1, currentPosition.getColumn());
            case WEST:
                return new Position(currentPosition.getRow(), currentPosition.getColumn()-1);
            case EAST:
                return new Position(currentPosition.getRow(), currentPosition.getColumn()+1);
            default:
                return null;
        }
    }
}
