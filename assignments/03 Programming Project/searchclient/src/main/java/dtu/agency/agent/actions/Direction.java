package dtu.agency.agent.actions;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

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
