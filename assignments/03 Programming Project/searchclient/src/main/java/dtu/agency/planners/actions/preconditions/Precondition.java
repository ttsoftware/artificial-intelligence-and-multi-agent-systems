package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Position;

public abstract class Precondition {

    private boolean isSatisfied;

    public Precondition() {}

    public boolean isSatisfied() {
        return isSatisfied;
    }

    public void setSatisfied(boolean satisfied) {
        isSatisfied = satisfied;
    }
}
