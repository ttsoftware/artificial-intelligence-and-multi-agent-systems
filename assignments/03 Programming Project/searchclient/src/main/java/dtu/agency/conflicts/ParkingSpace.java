package dtu.agency.conflicts;

import dtu.agency.board.Position;

public class ParkingSpace {

    private Position parkingSpaceOne;
    private Position parkingSpaceTwo;

    public ParkingSpace(Position positionOne, Position positionTwo) {
        parkingSpaceOne = positionOne;
        parkingSpaceTwo = positionTwo;
    }

    public ParkingSpace(Position positionOne) {
        parkingSpaceOne = positionOne;
    }

    public Position getParkingSpaceOne() {
        return parkingSpaceOne;
    }

    public Position getParkingSpaceTwo() {
        return parkingSpaceTwo;
    }
}
