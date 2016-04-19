package dtu.agency.events.client;

import dtu.agency.board.Agent;
import dtu.agency.events.Event;
import dtu.agency.planners.plans.ConcretePlan;

public class SendServerActionsEvent extends Event {

    private final Agent agent;
    private final ConcretePlan concretePlan;

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

    public Agent getAgent() {
        return agent;
    }
}