package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.services.BDIService;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.agentplanner.HLPlan;
import dtu.agency.planners.agentplanner.HLPlanner;
import dtu.agency.planners.htn.PrimitivePlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.EventBusService;

import java.util.Objects;

public class AgentThread implements Runnable {

    // the agent object which this agency corresponds to
    private final Agent agent;
    private final BDIService bdi;

    public AgentThread(Agent agent) {
        this.agent = agent;
        bdi = new BDIService(agent);
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

        HTNPlanner htnPlanner = new HTNPlanner(this.agent, new SolveGoalSuperAction(goal));
        int steps = htnPlanner.getBestPlanApproximation();

        bdi.getBids().put(goal.getLabel(), htnPlanner);

        System.err.println("Agent "+ agent.getLabel() +": received a goaloffer " + goal.getLabel() + " event and returned: " + Integer.toString(steps));

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

            System.err.println(agent.getLabel()+": I won the bidding for: " + event.getGoal().getLabel());

            // Find the HTNPlanner for this goal, and do the actual planning
            HTNPlanner htnPlanner = bdi.getBids().get(event.getGoal().getLabel());

            bdi.appendIntention(htnPlanner.getIntention());

            HLPlanner planner = new HLPlanner(bdi.getState(), htnPlanner);

            // Desire 1:  Find if possible a low level plan, and consider it a possible solution
            // NO RELAXATIONS!
            PrimitivePlan llPlan = htnPlanner.plan();

            // Desire 2:  Find a high level plan, and add to desires
            HLPlan hlPlan = planner.plan();
            if (hlPlan!=null) {
                System.err.println("Agent " +agent.getLabel()+ ": Found High Level Plan: " + hlPlan.toString());
                bdi.getCurrentIntention().setHighLevelPlan(hlPlan);
            }

            // Compare the length of the plans, and choose the shorter,
            // (and evolve) and return it

            if ( (llPlan==null) || (llPlan.size()==0) || (hlPlan.getHeuristic() < llPlan.size()) ) {
                // go with HLPlan
                // create total primitive plan from High level actions
                HLAction action;
                llPlan = new PrimitivePlan();
                while (!hlPlan.isEmpty()) {
                    action = hlPlan.poll();
                    htnPlanner = new HTNPlanner(agent, action);
                    llPlan.appendActions(htnPlanner.plan());
                }
            }
            // else go with PrimitivePlan already discovered

//            boolean oldDebugMode = DebugService.setDebugMode(true);
//            DebugService.setDebugMode(oldDebugMode);

            System.err.println("Agent " +agent.getLabel()+ ": Found Concrete Plan: " + llPlan.toString());
            // are we gonna submit the entire primitivePlan to the agency at once??
            // maybe it is better to divide the sending of plans into smaller packages,
            // e.g. solving separate intentions as GotoBox, MoveBox, etc.
            // this will give the agent the possibility of reacting to changes
            // in the environment.
            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), agent, llPlan)); // execute plan
        }
    }



    public Agent getAgent() {
        return agent;
    }
}