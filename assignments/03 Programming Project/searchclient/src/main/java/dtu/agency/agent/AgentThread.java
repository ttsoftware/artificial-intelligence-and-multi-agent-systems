package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.htn.HTNPlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.pop.POPPlan;
import dtu.agency.planners.pop.PartialOrderPlanner;
import dtu.agency.services.EventBusService;

import java.util.Hashtable;
import java.util.Objects;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private final Agent agent;
    private Hashtable<String, HTNPlan> htnPlans;

    public AgentThread(Agent agent) {
        this.agent = agent;
        htnPlans = new Hashtable<>();
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

        HTNPlanner htnPlanner = new HTNPlanner(agent, goal);
        HTNPlan plan = htnPlanner.plan();

        htnPlans.put(goal.getLabel(), plan);

        System.err.println(
                "Agent recieved a goaloffer " +
                        goal.getLabel() +
                        " event and returned estimation: " +
                        Integer.toString(plan.totalEstimatedDistance())
        );

        EventBusService.post(new GoalEstimationEvent(agent.getLabel(), plan.totalEstimatedDistance()));
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

            // Find the HTNPlan for this goal
            HTNPlan htnPlan = htnPlans.get(event.getGoal().getLabel());

            System.err.println(String.format("Number of abstract actions: %d", htnPlan.getActions().size()));

            htnPlan.getActions().forEach(abstractAction -> {
                PartialOrderPlanner popPlanner = new PartialOrderPlanner(abstractAction, agent);

                POPPlan plan = popPlanner.plan();

                // Post the partial plan to the agency
                EventBusService.post(new PlanOfferEvent(event.getGoal(), agent, plan));
            });
        }
    }

    public Agent getAgent() {
        return agent;
    }
}