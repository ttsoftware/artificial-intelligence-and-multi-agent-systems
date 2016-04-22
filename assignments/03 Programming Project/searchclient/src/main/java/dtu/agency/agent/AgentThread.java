package dtu.agency.agent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.agent.bdi.Ideas;
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
            // We won the bid for this goal!
            System.err.println(Thread.currentThread().getName() + ": Agent " + BDIService.getInstance().getAgent().getLabel() + ": I won the bidding for: " + event.getGoal().getLabel());

            BDIService.getInstance().addMeaningOfLife(event.getGoal()); // update the meaning of this agent's life

            PlanningLevelService pls = BDIService.getInstance().getLevelServiceAfterPendingPlans();

            Mind mind = new Mind(pls);

//            PrimitivePlan plan = mind.clearPath(event.getGoal()); // use ClearPathTest level as test environment
//            PrimitivePlan plan = mind.sandbox(); // use SAD1 level as test environment
            PrimitivePlan plan = mind.solve(event.getGoal()); // solves all levels (ideally)

            // TODO going from BDI v.3 --> BDI v.4 (REACTIVE AGENT)
            // are we gonna submit the entire primitivePlan to the agency at once??
            // maybe it is better to divide the sending of plans into smaller packages,
            // e.g. solving separate intentions as GotoBox, MoveBox, etc.
            // this will give the agent the possibility of reacting to changes
            // in the environment.

            // print status and communicate with agency
            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": Using Concrete Plan: " + plan.toString());

            // Add plan to map of goals and plans
            BDIService.getInstance().setCurrentlyExecutingPlan(plan);

            // Send the response back
            event.setResponse(plan);
        }
    }
}