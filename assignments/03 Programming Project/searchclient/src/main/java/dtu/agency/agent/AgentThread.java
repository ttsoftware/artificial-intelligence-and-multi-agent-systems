package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Agent;
import dtu.agency.events.agency.GoalOfferEventSubscriber;
import dtu.agency.events.agency.StopAllAgentsEvent;
import dtu.agency.planners.PartialOrderPlanner;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.services.EventBusService;
import dtu.agency.services.LevelService;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private Agent agent;

    public AgentThread(Agent agent) {
        this.agent = agent;
    }

    /**
     * Execute a specific action
     *
     * @param action Action
     */
    public void performAction(Action action) {
        boolean success = false;
        switch (action.getType()) {
            case MOVE: {
                // Update the level
                success = LevelService.getInstance().move(agent, (MoveAction) action);
                break;
            }
            case PUSH: {
                // Update the level
                success = LevelService.getInstance().push(agent, (PushAction) action);
                break;
            }
            case PULL: {
                // Update the level
                success = LevelService.getInstance().pull(agent, (PullAction) action);
                break;
            }
            case NONE: { // NONE
                // No need to modify the level
                success = true;
                break;
            }
        }
    }

    @Override
    public void run() {
        // Register for an event - announce that you want this event
        GoalOfferEventSubscriber goalOfferEventSubscriber = new GoalOfferEventSubscriber(agent.getLabel());
        EventBusService.getEventBus().register(goalOfferEventSubscriber);

        // Partial order agent
        PartialOrderPlanner popPlanner = new PartialOrderPlanner(new AbstractAction());

        // register all events handled by this class
        EventBusService.getEventBus().register(this);
    }

    /**
     * Stops the thread if this event is recieved
     *
     * @param event
     */
    @Subscribe
    public void stopEvent(StopAllAgentsEvent event) {
        Thread.currentThread().interrupt();
    }
}