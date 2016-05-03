package dtu.agency.conflicts;

import dtu.agency.board.Position;

public class ParkingSpace {

    private Position parkingSpace;
    private int actionIndexInPlan;

    public ParkingSpace(Position parkingSpacePosition, int actionIndexInPlan) {
        parkingSpace = parkingSpacePosition;
        this.actionIndexInPlan = actionIndexInPlan;
    }

    public Position getParkingSpace() {
        return parkingSpace;
    }

    public int getActionIndexInPlan() {
        return actionIndexInPlan;
    }
}
