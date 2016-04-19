package dtu.agency.agent.bdi;


import dtu.agency.planners.plans.HLPlan;

public class AgentIntention {
    // (top level) Intentions are really SolveGoalSuperActions()
    // but could also be other orders issued by TheAgency
    private final dtu.agency.actions.abstractaction.hlaction.HLAction intention;
    private HLPlan highLevelPlan;

    public AgentIntention(dtu.agency.actions.abstractaction.hlaction.HLAction topIntention) {
        highLevelPlan = new HLPlan();
        intention = topIntention;
    }

    public dtu.agency.actions.abstractaction.hlaction.HLAction getIntention() {
        return intention;
    }

    public HLPlan getHighLevelPlan() {
        return highLevelPlan;
    }

    public void setHighLevelPlan(HLPlan highLevelPlan) {
        this.highLevelPlan = highLevelPlan;
    }
}
