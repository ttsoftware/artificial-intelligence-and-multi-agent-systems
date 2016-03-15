package dtu.agent;

import dtu.agent.actions.Action;
import dtu.board.Agent;
import dtu.events.EventBusService;
import dtu.events.GoalOfferEvent;
import dtu.events.GoalOfferEventSubscriber;
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
        GoalOfferEventSubscriber goalOfferEventSubscriber = new GoalOfferEventSubscriber();
        EventBusService.getEventBus().register(goalOfferEventSubscriber);

        int steps = goalOfferEventSubscriber.getSteps();

        // Partial order planning
        PartialOrderPlanner popPlanner = new PartialOrderPlanner(new AbstractAction());

        // Post some other event indicating that you are done with your work
    }
}
