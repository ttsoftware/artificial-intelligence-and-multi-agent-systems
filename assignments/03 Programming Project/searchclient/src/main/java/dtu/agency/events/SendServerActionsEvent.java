package dtu.agency.events;

import dtu.agency.board.Agent;
import dtu.agency.planners.ConcretePlan;

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