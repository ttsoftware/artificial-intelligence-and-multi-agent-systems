package dtu.agency.planners.agentplanner;

import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.planners.htn.PrimitivePlan;

/**
 * Created by koeus on 4/9/16.
 */
public class AgentPlan {

    private final HLAction intention;
    private final PrimitivePlan plan;

    public AgentPlan( HLAction intention, PrimitivePlan plan) {
        this.intention = intention;
        this.plan = plan;
    }

    public PrimitivePlan getPlan() {
        return plan;
    }

    public HLAction getIntention() {
        return intention;
    }


}
