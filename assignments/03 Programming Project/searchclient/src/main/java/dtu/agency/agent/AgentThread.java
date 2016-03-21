package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agency.StopAllAgentsEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.HTNPlanner;
import dtu.agency.planners.PartialOrderPlanner;
import dtu.agency.services.EventBusService;
import dtu.agency.services.LevelService;

import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private Agent agent;
    private Hashtable<String, HTNPlan> htnPlans;

    public AgentThread(Agent agent) {
        this.agent = agent;
        htnPlans = new Hashtable<>();
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
        // register all events handled by this class
        EventBusService.getEventBus().register(this);
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
        HTNPlanner htnPlanner = new HTNPlanner(goal);
        HTNPlan plan = htnPlanner.plan();

        htnPlans.put(goal.getLabel(), plan);

        Random random = new Random();
        int randomSteps = random.nextInt();

        System.err.println("Agent recieved a goaloffer event and returned: " + Integer.toString(randomSteps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(agent.getLabel(), randomSteps));
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

            // Find the HTNPlan for this goal
            HTNPlan htnPlan = htnPlans.get(event.getGoal().getLabel());

            // Partial order plan
            htnPlan.getActions().forEach(abstractAction -> {
                PartialOrderPlanner popPlanner = new PartialOrderPlanner(abstractAction);
                // Post the partial plan to the agency
                EventBusService.getEventBus().post(new PlanOfferEvent(agent, popPlanner.plan()));
            });

        }
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