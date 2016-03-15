package dtu.agent;

import com.google.common.eventbus.Subscribe;
import dtu.events.Event;
import dtu.events.EventSubscriber;
import dtu.planners.HTNPlan;
import dtu.planners.HTNPlanner;
import dtu.planners.PartialOrderPlanner;
import dtu.planners.Plan;
import dtu.planners.firstorder.actions.Action;
import dtu.events.EventBusService;

import java.util.List;

public class AgentThread implements Runnable {

    public AgentThread() {
        //
    }

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
        PartialOrderPlanner popplanner = new PartialOrderPlanner(new HTNPlan());

        /*
        popPlans.forEach(plan1 -> {
            plan1.getActions().forEach(this::performAction);
        });
        */

        // some other event indicating that you are done with your work
        EventBusService.getEventBus().post(new Event());
    }
}
