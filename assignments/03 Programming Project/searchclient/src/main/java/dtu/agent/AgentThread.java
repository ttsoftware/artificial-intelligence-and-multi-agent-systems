package dtu.agent;

import com.google.common.eventbus.Subscribe;
import dtu.events.Event;
import dtu.events.EventBusService;
import dtu.planners.HTNPlan;
import dtu.planners.HTNPlanner;
import dtu.planners.PartialOrderPlanner;
import dtu.agent.actions.Action;
import dtu.planners.actions.AbstractAction;

public class AgentThread implements Runnable {

    public AgentThread() {
        //
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
        EventBusService.getEventBus().register(this);
    }

    @Subscribe
    public void change(Event event) {
        // Do something with the event

        // HTN planning
        HTNPlanner htnPlanner = new HTNPlanner();

        // Partial order planning
        PartialOrderPlanner popPlanner = new PartialOrderPlanner(new AbstractAction());

        // some other event indicating that you are done with your work
        EventBusService.getEventBus().post(new Event());
    }
}
