package dtu.agency.agent.bdi;


import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.agentplanner.HLPlan;

public class AgentIntention {
    // (top level) Intentions are really SolveGoalSuperActions()
    // but could also be other orders issued by TheAgency
    private final HLAction intention;
    private HLPlan highLevelPlan;

    public AgentIntention(HLAction topIntention) {
        highLevelPlan = new HLPlan();
        intention = topIntention;
    }

    public HLAction getIntention() {
        return intention;
    }

    public HLPlan getHighLevelPlan() {
        return highLevelPlan;
    }

    public void setHighLevelPlan(HLPlan highLevelPlan) {
        this.highLevelPlan = highLevelPlan;
    }
}
