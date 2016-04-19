package dtu.agency.events.client;

import dtu.agency.board.Agent;
import dtu.agency.events.AsyncEvent;
import dtu.agency.planners.plans.ConcretePlan;

public class SendServerActionsEvent extends AsyncEvent<Boolean> {

    private final Agent agent;
    private ConcretePlan concretePlan;

    /**
     * The plannerclient listens for these events, and forwards them to the server
     * @param concretePlan
     */
    public SendServerActionsEvent(Agent agent, ConcretePlan concretePlan) {
        this.agent = agent;
        this.concretePlan = concretePlan;
    }

    public ConcretePlan getConcretePlan() {
        return concretePlan;
    }

    public void setConcretePlan(ConcretePlan concretePlan) {
        this.concretePlan = concretePlan;
    }

    public Agent getAgent() {
        return agent;
    }
}