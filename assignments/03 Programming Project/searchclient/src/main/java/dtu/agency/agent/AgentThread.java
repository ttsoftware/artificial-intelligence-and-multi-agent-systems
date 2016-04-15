package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.Mind;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
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
        // TODO: important step - update the planning level service to match state after current plans are executed
        // pls.calculateStateAfterCurrentPlansHasBeenExecuted();

        Mind mind = new Mind(goal, pls);
        int approximatedSteps = mind.getBestApproximateDistance();
        Ideas ideas  = mind.getAllAbstractIdeas();

        BDIService.getInstance().getIdeas().put(goal.getLabel(), ideas);

        // TODO: Find number of remaining steps to be executed at this moment
        int remainingSteps = 0; // NOT 0, unless no plan awaits execution
        int totalSteps = approximatedSteps + remainingSteps;

        System.err.println(Thread.currentThread().getName()
                + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                + ": received a goaloffer " + goal.getLabel()
                + " event and returned: " + Integer.toString(totalSteps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(
                BDIService.getInstance().getAgent().getLabel(),
                totalSteps)
        );
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

            PrimitivePlan plan;

//            plan = test1(event); // use SAD1 level as test environment
            plan = sandbox(event); // use SAD1 level as test environment
//            plan = solve(event); // solves all levels (ideally)

            System.err.println("Agent " +BDIService.getInstance().getAgent().getLabel()+ ": Using Concrete Plan: " + plan.toString());

            /*
            // TODO going from BDI v.3 --> BDI v.4 (REACTIVE AGENT)
            // are we gonna submit the entire primitivePlan to the agency at once??
            // maybe it is better to divide the sending of plans into smaller packages,
            // e.g. solving separate intentions as GotoBox, MoveBox, etc.
            // this will give the agent the possibility of reacting to changes
            // in the environment.
            */

            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), BDIService.getInstance().getAgent(), plan)); // execute plan
        }

    }

    /**
     * This method tries to solve the level as best as possible at current state
     * @param event
     * @return
     */
    private PrimitivePlan solve(GoalAssignmentEvent event) {
        System.err.println("SOLVER is running - all levels should (ideally) be solved by this");
        Goal target = event.getGoal();
        Agent agent = BDIService.getInstance().getAgent();


        // Find the Ideas produced at bidding round for this goal
        Ideas ideas = BDIService.getInstance().getIdeas().get(target.getLabel());
        System.err.println("Ideas retrieved at solution round: "+ideas.toString());

        // update the meaning of this agent's life
        BDIService.getInstance().addMeaningOfLife(target);


        // Desire:  Find if possible a low level plan, and consider it a possible solution
        PrimitivePlan bestPlan = null;

        while (ideas.peekBest() != null) {
            SolveGoalAction bestIdea = ideas.getBest();

            PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

            HTNPlanner relaxedHTNPlanner = new HTNPlanner(pls, bestIdea, RelaxationMode.NoAgentsNoBoxes);
            HTNPlanner realHTNPlanner = new HTNPlanner(pls, bestIdea, RelaxationMode.NoAgents);

            /** START DEBUGGER **/
            DebugService.setDebugLevel(DebugService.DebugLevel.PICKED); // Decide amount of debugging statements printed
            boolean oldDebugMode = DebugService.setDebugMode(true);  // START DEBUGGER MESSAGES
            /** START DEBUGGER **/
            /** END DEBUGGER **/
            DebugService.setDebugMode(oldDebugMode);                 // END DEBUGGER MESSAGES
            /** END DEBUGGER **/

            PrimitivePlan relaxedPlan = relaxedHTNPlanner.plan();
            PrimitivePlan realPlan = realHTNPlanner.plan();

            PrimitivePlan currentPlan = null;

            if (realPlan!=null && relaxedPlan!=null){        // both plans are valid
                currentPlan = realPlan;                      // set real plan but do hlplanning
            } else if (relaxedPlan!=null) { // relaxed plan is usable and
                if (currentPlan!=null && realPlan.size() <= relaxedPlan.size()) { // use the shorter one
                    realHTNPlanner.commit();
                } else {
                    // TODO: Do high level planning (instead??)
                    System.err.println("Implement HL Planning here");
                    /*
                    // create a HLPlan
                    HLPlanner planner = new HLPlanner(bestIdea, pls1);
                    HLPlan hlPlan = planner.plan();

                    // checkout the resulting HLPlan
                    if (hlPlan!=null) {
                        System.err.println("Agent " + agent.getLabel()+ ": Found High Level Plan: " + hlPlan.toString());
                        BDIService.getInstance().getCurrentIntention().setHighLevelPlan(hlPlan);
                    }

                    // Compare the length of the plans, and choose the shorter, (and evolve) and return it

                    if (realPlan != null && realPlan.size() < hlPlan.estimate()) {
                        commit to realPlan
                    } else {
                        // evolve hlPlan to primitive plan
                        // OR
                        // store hlPlan along with its best estimate
                    }
                    */

                }
            } else { // the relaxed plan is null - the box /goal combination is unreachable
                System.err.println("skip this idea - the box/goal combination is unreachable");
            }

            // update the best plan
            if (bestPlan == null &&  currentPlan != null) {
                bestPlan = currentPlan;
            } else if (currentPlan != null && currentPlan.size() < bestPlan.size()) {
                bestPlan = currentPlan;
            }
        }

        // Check the result of this planning phase
        if (bestPlan != null) {
            System.err.println("Agent " + agent.getLabel() + ": Found Concrete Plan: " + bestPlan.toString());
        } else {
            System.err.println("Agent " + agent.getLabel() + ": Did not find a Concrete Plan.");
            bestPlan = new PrimitivePlan();
        }

        // TODO: store the resulting plans and states in BDIService.getInstance() after planning..??

        return bestPlan;
    }


    /**
     * This method is more or less a training/test method
     * For testing purposes, like what happens if i simply task the agent with going 3 steps north...
     * @param event
     * @return
     */
    private PrimitivePlan test1(GoalAssignmentEvent event) {
        System.err.println("TEST 1 is running - make sure that the level is SAD1");
        Goal target = event.getGoal();

        PrimitivePlan plan = new PrimitivePlan();
        plan.addAction(new MoveConcreteAction(Direction.EAST));
        plan.addAction(new MoveConcreteAction(Direction.EAST));
        plan.addAction(new MoveConcreteAction(Direction.EAST));
        plan.addAction(new MoveConcreteAction(Direction.EAST));
        return plan;
    }

    /**
     * This method is more or less a training/test method
     * @param event
     * @return
     */
    private PrimitivePlan sandbox(GoalAssignmentEvent event) {
        System.err.println("SANDBOX is running - make sure that the level is SAD1");
        Goal targetGoal = event.getGoal();
        Box targetBox = new Box("A0");

        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

        HMoveBoxAction mba1 = new HMoveBoxAction(targetBox, new Position(1,17), new Position(1,1));
        HTNPlanner htn1 = new HTNPlanner(pls, mba1, RelaxationMode.NoAgents);
        PrimitivePlan plan1 = htn1.plan();
        System.err.println("Plan 1:\n" + plan1);
        htn1.commit(); // update positions in PlanningLevelService pls

        System.err.println("after plan 1 - pls: agent:"+pls.getAgentPosition()+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba2 = new HMoveBoxAction(targetBox, new Position(2,7), pls.getPosition(targetBox));
        HTNPlanner htn2 = new HTNPlanner(pls, mba2, RelaxationMode.NoAgents);
        PrimitivePlan plan2 = htn2.plan();
        System.err.println("Plan 2:\n" + plan2);
        htn2.commit();

        System.err.println("after plan 2 - pls: agent:"+pls.getAgentPosition()+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba3 = new HMoveBoxAction(targetBox, new Position(5,17), pls.getPosition(targetBox));
        HTNPlanner htn3 = new HTNPlanner(pls, mba3, RelaxationMode.NoAgents);
        PrimitivePlan plan3 = htn3.plan();
        System.err.println("Plan 3:\n" + plan3);
        htn2.commit();

        System.err.println("after plan 3 - pls: agent:"+pls.getAgentPosition()+ " Box:" +pls.getPosition(targetBox) );

        plan1.appendActions(plan2);
        plan1.appendActions(plan3);
        System.err.println(plan1);

        /** START DEBUGGER **/
        DebugService.setDebugLevel(DebugService.DebugLevel.PICKED); // Decide amount of debugging statements printed
        boolean oldDebugMode = DebugService.setDebugMode(true);  // START DEBUGGER MESSAGES
        /** START DEBUGGER **/
        /** END DEBUGGER **/
        DebugService.setDebugMode(oldDebugMode);                 // END DEBUGGER MESSAGES
        /** END DEBUGGER **/

        return plan1;
    }



}