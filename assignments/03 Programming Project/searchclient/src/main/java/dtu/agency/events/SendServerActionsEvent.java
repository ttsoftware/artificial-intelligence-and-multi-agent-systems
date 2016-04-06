package dtu.agency.events;

import dtu.agency.actions.ConcreteAction;

import java.util.Stack;

public class SendServerActionsEvent extends Event {

    private final Stack<ConcreteAction> concreteActions;

    /**
     * The client listens for these events, and forwards them to the server
     * @param concreteActions Must be a list equal to the number of agents
     */
    public SendServerActionsEvent(Stack<ConcreteAction> concreteActions) {
        this.concreteActions = concreteActions;
    }

    public Stack<ConcreteAction> getConcreteActions() {
        return concreteActions;
    }
}