package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.htn.*;
import dtu.agency.services.BDIService;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.agentplanner.HLPlan;
import dtu.agency.planners.agentplanner.HLPlanner;
import dtu.agency.services.DebugService;
import dtu.agency.services.EventBusService;

import java.util.Objects;

public class AgentThread implements Runnable {

    // the agent object which this agency corresponds to
    private final Agent agent;
    private final BDIService bdi;

    public AgentThread(Agent agent) {
        this.agent = agent;
        bdi = new BDIService(agent); // ThreadLocal!!!
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

        HTNGoalPlanner htnPlanner = new HTNGoalPlanner(this.agent, goal);
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

            // Find the HTNPlanner used to bid for this goal
            HTNPlanner htnPlanner = bdi.getBids().get(event.getGoal().getLabel());
            // update the intention of this agent (by appending it)
            bdi.appendIntention(htnPlanner.getIntention());

            HTNPlanner htn2 = new HTNPlanner(htnPlanner);

            HTNNode inode = new HTNNode(htnPlanner.getInitialNode());
            HTNState istate = inode.getState();
            MixedPlan iremHlas = inode.getRemainingPlan();
//            System.err.println(inode.toString() + "\n"+ istate.toString() + "\n" + iremHlas.toString());
            System.err.println("htn1" + htnPlanner.toString());

            // Desire 1:  Find if possible a low level plan, and consider it a possible solution
            // TODO: Important to plan with NO relaxations here!!!!
            PrimitivePlan llPlan = htnPlanner.plan();
            System.err.println("Agent " +agent.getLabel()+ ": Found Concrete Plan: " + llPlan.toString());

//            System.err.println(inode.toString() + "\n"+ istate.toString() + "\n" + iremHlas.toString());
            System.err.println("htn1" + htnPlanner.toString());
            System.err.println("htn2" + htn2.toString());
            llPlan = htn2.plan();
            System.err.println("htn2" + htn2.toString());

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
            if ( (llPlan==null) || (llPlan.size()==0) || (hlPlan.getHeuristic() <= llPlan.size()+5) ) {
                // go with HLPlan
                System.err.println("Deriving concrete plan from HL plan..");
                // create total primitive plan from High level actions
                HLAction action;
                llPlan = new PrimitivePlan();
                while (!hlPlan.isEmpty()) {
                    action = hlPlan.poll();
                    htnPlanner = new HTNPlanner(agent, action, RelaxationMode.NoAgentsNoBoxes);
                    // TODO: NEED a correct implementation using PlanningLevelService
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

            // store the resulting plans and states in bdiservice after planning..


            System.err.println("Agent " +agent.getLabel()+ ": Using Concrete Plan: " + llPlan.toString());
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