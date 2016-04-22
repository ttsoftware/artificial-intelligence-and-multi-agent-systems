package dtu.agency.agent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.planners.Mind;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.PlanningLevelService;

public class AgentThread implements Runnable {

    @Override
    public void run() {
        // register all events handled by this class
        EventBusService.register(this);
        System.err.println(Thread.currentThread().getName() + ": Started agent: " + BDIService.getInstance().getAgent().getLabel());
    }

    /**
     * The Agency offered a goal - we bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        Goal goal = event.getGoal();

        // calculate positions of self + boxes, when current plans are executed
        // this as basis on bidding on the next
        // TODO: important step - update the planning level service to match state after current plans are executed
        PlanningLevelService pls = BDIService.getInstance().getLevelServiceAfterPendingPlans();

        int remainingSteps = BDIService.getInstance().remainingConcreteActions();

        Mind mind = new Mind(pls);
        Ideas ideas = mind.thinkOfIdeas(goal); // they are automatically stored in BDIService
        AgentIntention intention = mind.filter(ideas, goal);

        BDIService.getInstance().getIntentions().put(goal.getLabel(), intention);

        int totalSteps = remainingSteps + intention.getApproximateSteps();

        // print status and communicate with agency
        System.err.println(Thread.currentThread().getName()
                + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                + ": received a goaloffer " + goal.getLabel()
                + " event and returned: " + Integer.toString(totalSteps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(
                        BDIService.getInstance().getAgent(),
                        goal,
                        totalSteps
                )
        );
    }

    /**
     * The Agency assigned someone a goal
     *
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (event.getAgent().getLabel().equals(BDIService.getInstance().getAgent().getLabel())) {
            // setup local variables
            BDIService mind = BDIService.getInstance();
            Agent agent = mind.getAgent();

            // We won the bid for this goal!
            Goal goal = event.getGoal();
            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": I won the bidding for: " + goal );

            boolean successful = mind.solveGoal(goal); // generate a plan internal in the agents consciousness.

            PrimitivePlan plan;
            if (successful) {
                // this updates the BDI internally in the agent
                // TODO going from BDI v.3 --> BDI v.4 (REACTIVE AGENT)
                // are we gonna submit the entire primitivePlan to the agency at once??
                // maybe it is better to divide the sending of plans into smaller packages,
                // e.g. solving separate intentions as GotoBox, MoveBox, etc.
                // this will give the agent the possibility of reacting to changes
                // in the environment.
                plan = mind.calculateNextSteps();
            } else {
                // TODO: failed what to do... - respond with failure??
                System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid HLPlan for: " + goal );
                plan = new PrimitivePlan();
            }

            // print status and communicate with agency
            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": Using Concrete Plan: " + plan.toString());

            // Send the response back
            event.setResponse(plan);
        }
    }
}