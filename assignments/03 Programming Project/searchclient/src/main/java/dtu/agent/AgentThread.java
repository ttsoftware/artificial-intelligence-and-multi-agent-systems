package dtu.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agent.actions.Action;
import dtu.board.Agent;
import dtu.events.EventBusService;
import dtu.events.agent.GoalOfferEventSubscriber;
import dtu.events.agent.StopAllAgentsEvent;
import dtu.planners.PartialOrderPlanner;
import dtu.planners.actions.AbstractAction;

public class AgentThread implements Runnable {

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

        // Partial order planning
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