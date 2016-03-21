package dtu.agency.events;

import dtu.agency.agent.actions.Action;

import java.util.List;

public class SendServerActionsEvent extends Event {

    private final List<Action> actions;

    /**
     * The client listens for these events, and forwards them to the server
     * @param actions Must be a list equal to the number of agents
     */
    public SendServerActionsEvent(List<Action> actions) {
        this.actions = actions;
    }

    public List<Action> getActions() {
        return actions;
    }
}