package dtu.agency.planners.htn.heuristic;

/**
 * Created by mads on 3/25/16.
 */
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

        throw new UnsupportedOperationException("Invalid direction object.");
    }
}