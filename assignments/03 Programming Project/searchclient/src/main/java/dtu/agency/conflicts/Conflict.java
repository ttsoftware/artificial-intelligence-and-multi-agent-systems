package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.GlobalLevelService;

public class Conflict {

    private Agent agentOne;
    private ConcretePlan agentOnePlan;

    private Agent agentTwo;
    private ConcretePlan agentTwoPlan;

    public Conflict(Agent agentOne, ConcretePlan agentOnePlan, Agent agentTwo, ConcretePlan agentTwoPlan) {
        this.agentOne = agentOne;
        this.agentOnePlan = agentOnePlan;
        this.agentTwo = agentTwo;
        this.agentTwoPlan = agentTwoPlan;
    }

    public Conflict(Integer agentOne, ConcretePlan agentOnePlan, Integer agentTwo, ConcretePlan agentTwoPlan) {
        GlobalLevelService levelService = GlobalLevelService.getInstance();
        this.agentOne = levelService.getAgent(agentOne.toString());
        this.agentOnePlan = agentOnePlan;

        this.agentTwo = levelService.getAgent(agentTwo.toString());
        this.agentTwoPlan = agentTwoPlan;
    }

    public Agent getFastest() {
        return (agentOnePlan.getActions().size() <= agentTwoPlan.getActions().size()) ? agentOne : agentTwo;
    }

    public Agent getSlowest() {
        return (agentOnePlan.getActions().size() > agentTwoPlan.getActions().size()) ? agentOne : agentTwo;
    }

    public Agent getAgentOne() {
        return agentOne;
    }

    public ConcretePlan getAgentOnePlan() {
        return agentOnePlan;
    }

    public Agent getAgentTwo() {
        return agentTwo;
    }

    public ConcretePlan getAgentTwoPlan() {
        return agentTwoPlan;
    }
}
