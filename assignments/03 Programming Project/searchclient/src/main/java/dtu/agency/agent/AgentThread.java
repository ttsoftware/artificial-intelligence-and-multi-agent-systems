package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.actions.Action;
import dtu.agency.board.Agent;
import dtu.agency.services.EventBusService;
import dtu.agency.events.agent.GoalOfferEventSubscriber;
import dtu.agency.events.agent.StopAllAgentsEvent;
import dtu.agency.planners.PartialOrderPlanner;
import dtu.agency.planners.actions.AbstractAction;

public class AgentThread implements Runnable {

    // the agent object which this agent corresponds to
    private Agent agent;

    public AgentThread(Agent agent) {
        this.agent = agent;
    }

    /**
     * Execute a specific action
     * @param action Action
     */
    public void performAction(Action action) {
        switch (action.getType()) {
            case MOVE: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PUSH: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            case PULL: {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            default: { // NONE
                // Do nothing?
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        }
    }

    @Override
    public void run() {
        // Register for an event - announce that you want this event
        GoalOfferEventSubscriber goalOfferEventSubscriber = new GoalOfferEventSubscriber(agent.getLabel());
        EventBusService.getEventBus().register(goalOfferEventSubscriber);

        // Partial order agency
        PartialOrderPlanner popPlanner = new PartialOrderPlanner(new AbstractAction());

        // register all events handled by this class
        EventBusService.getEventBus().register(this);
    }

    /**
     * Stops the thread if this event is recieved
     * @param event
     */
    @Subscribe
    public void stopEvent(StopAllAgentsEvent event) {
        Thread.currentThread().interrupt();
    }
}