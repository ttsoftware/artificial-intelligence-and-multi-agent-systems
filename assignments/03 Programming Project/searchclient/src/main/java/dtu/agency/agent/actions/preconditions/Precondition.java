package dtu.agency.agent.actions.preconditions;

import java.io.Serializable;

public abstract class Precondition implements Serializable {

    private boolean isSatisfied;

    public Precondition() {}

    public boolean isSatisfied() {
        return isSatisfied;
    }

    public void setSatisfied(boolean satisfied) {
        isSatisfied = satisfied;
    }
}
