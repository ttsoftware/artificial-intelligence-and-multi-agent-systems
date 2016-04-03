package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agency.StopAllAgentsEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.EventBusService;

import java.util.Hashtable;
import java.util.Objects;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private Agent agent;
    private Hashtable<String, HTNPlanner> htnPlanners;

    public AgentThread(Agent agent) {
        this.agent = agent;
        htnPlanners = new Hashtable<>();
    }

    @Override
    public void run() {
        // register all events handled by this class
        EventBusService.getEventBus().register(this);
        // keep thread running until stop event.
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * The Agency offered a goal - we bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        Goal goal = event.getGoal();

        // HTN plan?
        HTNPlanner htnPlanner = new HTNPlanner(this.agent, goal);
        HTNPlan plan = htnPlanner.getBestPlan();

        htnPlanners.put(goal.getLabel(), htnPlanner);

        int steps = plan.getActions().size();

        System.err.println("Agent received a goaloffer " + goal.getLabel() + " event and returned: " + Integer.toString(steps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(agent.getLabel(), steps));
    }

    /**
     * The Agency assigned someone a goal
     *
     * @param event
     */
    @Subscribe
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (Objects.equals(event.getAgentLabel(), agent.getLabel())) {
            // We won the bid for this goal!

            System.err.println("I won the bid for: " + event.getGoal().getLabel());

            // Find the HTNPlanner for this goal
            HTNPlanner htnPlanner = htnPlanners.get(event.getGoal().getLabel());

            PrimitivePlan primitivePlan = htnPlanner.plan();

            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), agent, primitivePlan));
        }
    }

    /**
     * Stops the thread if this event is recieved
     *
     * @param event
     */
    @Subscribe
    public void stopEvent(StopAllAgentsEvent event) {
        System.err.println("Agent: " + agent.getLabel() + " recieved stop event");
        Thread.currentThread().interrupt();
    }
}