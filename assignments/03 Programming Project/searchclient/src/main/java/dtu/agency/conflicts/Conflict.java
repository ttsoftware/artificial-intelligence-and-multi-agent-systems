package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.GlobalLevelService;

public class Conflict {

    private Agent conceder;
    private ConcretePlan concederPlan;

    private Agent initiator;
    private ConcretePlan initiatorPlan;

    public Conflict(Agent agentOne, ConcretePlan agentOnePlan, Agent agentTwo, ConcretePlan agentTwoPlan) {

        if (agentOnePlan.getActions().size() < agentTwoPlan.getActions().size()) {
            conceder = agentOne;
            concederPlan = agentOnePlan;
            initiator = agentTwo;
            initiatorPlan = agentTwoPlan;
        } else {
            conceder = agentTwo;
            concederPlan = agentTwoPlan;
            initiator = agentOne;
            initiatorPlan = agentOnePlan;
        }
    }

    public Conflict(Integer agentOne, ConcretePlan agentOnePlan, Integer agentTwo, ConcretePlan agentTwoPlan) {

        if (agentOnePlan == null) {
            conceder = GlobalLevelService.getInstance().getAgent(agentTwo);
            concederPlan = agentTwoPlan;
            initiator = GlobalLevelService.getInstance().getAgent(agentOne);
            initiatorPlan = new PrimitivePlan();
        } else if (agentTwoPlan == null) {
            conceder = GlobalLevelService.getInstance().getAgent(agentOne);
            concederPlan = agentOnePlan;
            initiator = GlobalLevelService.getInstance().getAgent(agentTwo);
            initiatorPlan = new PrimitivePlan();
        } else if (agentOnePlan.getActions().size() < agentTwoPlan.getActions().size()) {
            conceder = GlobalLevelService.getInstance().getAgent(agentOne);
            concederPlan = agentOnePlan;
            initiator = GlobalLevelService.getInstance().getAgent(agentTwo);
            initiatorPlan = agentTwoPlan;
        } else {
            conceder = GlobalLevelService.getInstance().getAgent(agentTwo);
            concederPlan = agentTwoPlan;
            initiator = GlobalLevelService.getInstance().getAgent(agentOne);
            initiatorPlan = agentOnePlan;
        }
    }

    public Agent getConceder() {
        return conceder;
    }

    public ConcretePlan getConcederPlan() {
        return concederPlan;
    }

    public Agent getInitiator() {
        return initiator;
    }

    public ConcretePlan getInitiatorPlan() {
        return initiatorPlan;
    }

    public void setConceder(Agent conceder) {
        this.conceder = conceder;
    }

    public void setConcederPlan(ConcretePlan concederPlan) {
        this.concederPlan = concederPlan;
    }

    public void setInitiator(Agent initiator) {
        this.initiator = initiator;
    }

    public void setInitiatorPlan(ConcretePlan initiatorPlan) {
        this.initiatorPlan = initiatorPlan;
    }
}
