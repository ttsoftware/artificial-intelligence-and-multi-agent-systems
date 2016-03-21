package dtu.agency.agent.actions;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Direction getInverse() {
        switch (this) {
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
        }

        throw new UnsupportedOperationException("Invalid direction object.");
    }

    @Override
    public String toString() {
        switch (this) {
            case NORTH:
                return "N";
            case SOUTH:
                return "S";
            case EAST:
                return "E";
            case WEST:
                return "W";
        }

        throw new UnsupportedOperationException("Invalid direction object.");
    }
}
