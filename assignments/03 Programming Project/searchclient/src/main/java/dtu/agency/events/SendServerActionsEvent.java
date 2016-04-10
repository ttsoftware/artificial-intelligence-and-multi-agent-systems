package dtu.agency.events;

import dtu.agency.planners.ConcretePlan;

public class SendServerActionsEvent extends Event {

    private final ConcretePlan concretePlan;

    /**
     * The plannerclient listens for these events, and forwards them to the server
     * @param concretePlan
     */
    public SendServerActionsEvent(ConcretePlan concretePlan) {
        this.concretePlan = concretePlan;
    }

    public ConcretePlan getConcretePlan() {
        return concretePlan;
    }
}