package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.events.Event;
import dtu.agency.planners.ConcretePlan;

public class PlanOfferEvent extends Event {

    private final Agent agent;
    private final ConcretePlan plan;

    /**
     *
     * @param agent
     * @param plan
     */
    public PlanOfferEvent(Agent agent, ConcretePlan plan) {
        this.agent = agent;
        this.plan = plan;
    }

    public Agent getAgent() {
        return agent;
    }

    public ConcretePlan getPlan() {
        return plan;
    }
}
