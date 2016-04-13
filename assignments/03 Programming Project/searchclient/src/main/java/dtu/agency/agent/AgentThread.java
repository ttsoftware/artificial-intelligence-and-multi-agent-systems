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

        EventBusService.getEventBus().post(new GoalEstimationEvent(BDIService.getInstance().getAgent().getLabel(), steps));
    }

    /**
     * The Agency assigned someone a goal
     *
     * @param event
     */
    @Subscribe
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (event.getAgentLabel().equals(BDIService.getInstance().getAgent().getLabel())) {
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

            /*
            // start a new high level planning phase
            // Desire 2:  Find a high level plan, and add to desires
            HLPlanner planner = new HLPlanner(htnPlanner);
            HLPlan hlPlan = planner.plan();
            if (hlPlan!=null) {
                System.err.println("Agent " +agent.getLabel()+ ": Found High Level Plan: " + hlPlan.toString());
                bdi.getCurrentIntention().setHighLevelPlan(hlPlan);
            }

            // Compare the length of the plans, and choose the shorter,
            // (and evolve) and return it
            if ( (llPlan==null) || (llPlan.size()==0) || (hlPlan.approximateSteps() <= llPlan.size()+5) ) {
                // go with HLPlan
                System.err.println("Deriving concrete plan from HL plan..");
                // create total primitive plan from High level actions
                HLAction action;
                llPlan = new PrimitivePlan();
                while (!hlPlan.isEmpty()) {
                    action = hlPlan.poll();
                    htnPlanner = new HTNPlanner(agent, action, RelaxationMode.NoAgentsNoBoxes);
                    // TODO: NEED a correct implementation using BDILevelService
                    // TODO: replacing GlobalLevelService.
                    htnPlanner = new HTNPlanner(agent, action, RelaxationMode.None);
                    // tools for enable/disable debug printing mode
//                    boolean oldDebugMode = DebugService.setDebugMode(true);
                    PrimitivePlan plan = htnPlanner.plan();
//                    DebugService.setDebugMode(oldDebugMode);
                    llPlan.appendActions(plan);
                }
            }
            // else go with PrimitivePlan already discovered

            // store the resulting plans and states in BDIService.getInstance() after planning..

            System.err.println("Agent " +agent.getLabel()+ ": Using Concrete Plan: " + llPlan.toString());
            // are we gonna submit the entire primitivePlan to the agency at once??
            // maybe it is better to divide the sending of plans into smaller packages,
            // e.g. solving separate intentions as GotoBox, MoveBox, etc.
            // this will give the agent the possibility of reacting to changes
            // in the environment.
            */

            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), BDIService.getInstance().getAgent(), llPlan)); // execute plan
        }
    }
}