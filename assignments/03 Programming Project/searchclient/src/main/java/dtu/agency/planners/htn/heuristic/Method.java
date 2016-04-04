package dtu.agency.planners.htn.heuristic;

public enum Method {
    MANHATTAN,
    EUCLIDEAN;

    @Override
    public String toString() {
        switch (this) {
            case MANHATTAN:
                return "Manhattan";
            case EUCLIDEAN:
                return "Flight";
        }
        throw new UnsupportedOperationException("Invalid Method object.");
    }
}