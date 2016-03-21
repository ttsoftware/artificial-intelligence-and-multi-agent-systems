package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.events.Event;
import dtu.agency.planners.Plan;

public class PlanOfferEvent extends Event {

    private final Agent agent;
    private final Plan plan;

    /**
     *
     * @param agent
     * @param plan
     */
    public PlanOfferEvent(Agent agent, Plan plan) {
        this.agent = agent;
        this.plan = plan;
    }

    public Agent getAgent() {
        return agent;
    }

    public Plan getPlan() {
        return plan;
    }
}
