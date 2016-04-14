package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.Mind;
import dtu.agency.planners.hlplanner.HLPlanner;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.DebugService;
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
        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
        // TODO: important step - update the planning level service to
        // pls.calculateStateAfterCurrentPlansHasBeenExecuted();

        Mind mind = new Mind(goal, pls);
        int approximatedSteps = mind.getBestApproximateDistance();
        Ideas ideas  = mind.getAllAbstractIdeas();

        BDIService.getInstance().getIdeas().put(goal.getLabel(), ideas);

        // TODO: Find number of remaining steps to be executed at this moment
        int remainingSteps = 0; // NOT 0, unless no plan awaits execution
        int totalSteps = approximatedSteps + remainingSteps;

        System.err.println(Thread.currentThread().getName() + ": Agent " + BDIService.getInstance().getAgent().getLabel() + ": received a goaloffer " + goal.getLabel() + " event and returned: " + Integer.toString(totalSteps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(BDIService.getInstance().getAgent().getLabel(), totalSteps));
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
            Goal target = event.getGoal();

            System.err.println(Thread.currentThread().getName() + ": Agent " + BDIService.getInstance().getAgent().getLabel() + ": I won the bidding for: " + event.getGoal().getLabel());

            // Find the Ideas produced at bidding round for this goal
            Ideas ideas = BDIService.getInstance().getIdeas().get(target.getLabel());
            SolveGoalAction bestIdea = ideas.getBest();

//            System.err.println("Ideas retrieved at solution round: "+ideas.toString());
            // update the meaning of this agent's life
            BDIService.getInstance().addMeaningOfLife(target);

            // Desire 1:  Find if possible a low level plan, and consider it a possible solution
            // TODO: Important to plan with NO relaxations here!!!!
            PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

//            boolean oldDebugMode = DebugService.setDebugMode(true);
            HTNPlanner htnPlanner = new HTNPlanner(pls, bestIdea, RelaxationMode.NoAgentsNoBoxes);
//            DebugService.setDebugMode(oldDebugMode);

            PrimitivePlan plan = htnPlanner.plan();
            if (plan != null) {
                System.err.println("Agent " + BDIService.getInstance().getAgent().getLabel() + ": Found Concrete Plan: " + plan.toString());
            } else {
                System.err.println("Agent " + BDIService.getInstance().getAgent().getLabel() + ": Did not find a Concrete Plan.");
            }

            // start a new high level planning phase
            // Desire 2:  Find a high level plan, and add to desires
//            PlanningLevelService pls1 = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
//            HLPlanner planner = new HLPlanner(bestIdea, pls1);
//            boolean oldDebugMode = DebugService.setDebugMode(true);
//            HLPlan hlPlan = planner.plan();
//            DebugService.setDebugMode(oldDebugMode);
//            if (hlPlan!=null) {
//                System.err.println("Agent " +BDIService.getInstance().getAgent().getLabel()+ ": Found High Level Plan: " + hlPlan.toString());
//                BDIService.getInstance().getCurrentIntention().setHighLevelPlan(hlPlan);
//            }

            /*
            // Compare the length of the plans, and choose the shorter,
            // (and evolve) and return it
            if ( (llPlan==null) || (llPlan.size()==0) || (hlPlan.approximateSteps() <= llPlan.size()+5) ) {
                // go with HLAction
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
                    PrimitivePlan plan = htnPlanner.plan();
//                    boolean oldDebugMode = DebugService.setDebugMode(true);
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

            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), BDIService.getInstance().getAgent(), plan)); // execute plan
        }
    }
}