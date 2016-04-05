package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.EventBusService;

import java.util.HashMap;
import java.util.Objects;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private final Agent agent;
    private HashMap<String, HTNPlanner> htnPlanners;

    public AgentThread(Agent agent) {
        this.agent = agent;
        htnPlanners = new HashMap<>();
    }

    @Override
    public void run() {
        // register all events handled by this class
        EventBusService.register(this);
        System.err.println("Started agent: " + agent.getLabel());
    }

    /**
     * The Agency offered a goal - we bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        Goal goal = event.getGoal();

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

    public Agent getAgent() {
        return agent;
    }
}