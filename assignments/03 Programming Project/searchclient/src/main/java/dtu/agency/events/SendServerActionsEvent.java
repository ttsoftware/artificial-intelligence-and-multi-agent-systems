package dtu.agency.events;

import dtu.agency.agent.actions.Action;

import java.util.Stack;

public class SendServerActionsEvent extends Event {

    private final Stack<Action> actions;

    /**
     * The client listens for these events, and forwards them to the server
     * @param actions Must be a list equal to the number of agents
     */
    public SendServerActionsEvent(Stack<Action> actions) {
        this.actions = actions;
    }

    public Stack<Action> getActions() {
        return actions;
    }
}