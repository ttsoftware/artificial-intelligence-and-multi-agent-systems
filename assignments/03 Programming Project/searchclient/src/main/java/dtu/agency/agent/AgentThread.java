package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.htn.HTNGoalPlanner;
import dtu.agency.planners.htn.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;

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

        HTNGoalPlanner htnPlanner = new HTNGoalPlanner(goal);
        int steps = htnPlanner.getBestPlanApproximation();

        BDIService.getInstance().getBids().put(goal.getLabel(), htnPlanner);

        System.err.println(Thread.currentThread().getName() + ": Agent " + BDIService.getInstance().getAgent().getLabel() + ": received a goaloffer " + goal.getLabel() + " event and returned: " + Integer.toString(steps));

        EventBusService.post(new GoalEstimationEvent(BDIService.getInstance().getAgent(), steps));
    }

    /**
     * The Agency assigned someone a goal
     *
     * @param event
     */
    @Subscribe
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (event.getAgent().getLabel().equals(BDIService.getInstance().getAgent().getLabel())) {
            // We won the bid for this goal!

            System.err.println(Thread.currentThread().getName() + ": Agent " + BDIService.getInstance().getAgent().getLabel() + ": I won the bidding for: " + event.getGoal().getLabel());

            // Find the HTNPlanner used to bid for this goal
            HTNGoalPlanner htnPlanner = BDIService.getInstance().getBids().get(event.getGoal().getLabel());
            // update the intention of this agent (by appending it)
            BDIService.getInstance().appendIntention(htnPlanner.getIntention());

            System.err.println("htn1" + htnPlanner.toString());

            // Desire 1:  Find if possible a low level plan, and consider it a possible solution
            // TODO: Important to plan with NO relaxations here!!!!
            PrimitivePlan llPlan = htnPlanner.plan();
            System.err.println("Agent " + BDIService.getInstance().getAgent().getLabel() + ": Found Concrete Plan: " + llPlan.toString());

            // respond with plan
            // event.setResponse(llPlan);
            EventBusService.post(new PlanOfferEvent(event.getGoal(), BDIService.getInstance().getAgent(), llPlan)); // execute plan
        }
    }
}