package dtu.agency.board;

import dtu.agency.agent.actions.Direction;

public class Box extends BoardObject {

    private boolean isStationary;
    private Direction currentMovingDirection;

    public Box(String label) {
        super(label);
        this.isStationary = false;
        this.currentMovingDirection = Direction.NORTH;
    }

    public boolean isStationary() {
        return isStationary;
    }

    public void setStationary(boolean stationary) {
        isStationary = stationary;
    }

    public Direction getCurrentMovingDirection() {
        return currentMovingDirection;
    }

    public void setCurrentMovingDirection(Direction currentMovingDirection) {
        this.currentMovingDirection = currentMovingDirection;
    }
}
