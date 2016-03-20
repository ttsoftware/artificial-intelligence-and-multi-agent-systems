package dtu.agency.events.agency;

import dtu.agency.agent.actions.Action;
import dtu.agency.events.Event;

public class ActionOfferEvent extends Event {

    private final Action action;

    /**
     * An agent offers an Action to the Agency
     * @param action The action to perform
     */
    public ActionOfferEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}