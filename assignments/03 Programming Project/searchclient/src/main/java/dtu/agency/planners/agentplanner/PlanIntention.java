package dtu.agency.planners.agentplanner;


import dtu.agency.actions.abstractaction.HLAction;

import java.util.LinkedList;

public class PlanIntention {
    // Intentions are really High Level SolveGoalActions()
    private final HLAction topLevelIntention; // orders from the agency (or other agents)
    private HLAction currentIntention;

    public PlanIntention(HLAction intention) {
        topLevelIntention = intention;
    }

    public HLAction getTopLevelIntention() {
        return topLevelIntention;
    }

    public HLAction getCurrentIntention() {
        return currentIntention;
    }

    public void setCurrentIntention(HLAction currentIntention) {
        this.currentIntention = currentIntention;
    }
}
