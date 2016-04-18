package dtu.agency.planners;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.hlplanner.HLPlanner;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * This Planner creates the entire list of high level actions, that may get the job done.
 */
public class Mind {
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }
//    DebugService.setDebugLevel(DebugService.DebugLevel.PICKED); // ***     DEBUGGING LEVEL     ***
//    boolean oldDebugMode = DebugService.setDebugMode(true);     // *** START DEBUGGER MESSAGES ***
//    DebugService.setDebugMode(oldDebugMode);                    // ***  END DEBUGGER MESSAGES  ***

    private final Agent agent = BDIService.getInstance().getAgent();
    private final PlanningLevelService pls;        // of the current state of mind (after executing intended plans)

    /**
     * Constructor: All the Mind needs is the next goal and the beliefs of the agent solving it...
     */
    public Mind(PlanningLevelService pls) {
        debug("Mind initializing...");
        this.pls = pls;
    }

    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     * @param goal The goal targeted
     * @return The ideas (list of SolveGoalActions) that could potentially solve this goal.
     */
    public Ideas thinkOfIdeas(Goal goal) {
        debug("getIdeas(): ", 2);
        Ideas ideas = new Ideas(goal, pls); // agent destination is used for heuristic purpose

        for (Box box : pls.getLevel().getBoxes()) {
            debug(box.getLabel().substring(0,1).toLowerCase() + "=?" + goal.getLabel().toLowerCase().substring(0,1),2);
            if (box.getLabel().toLowerCase().substring(0,1).equals(goal.getLabel().toLowerCase().substring(0,1))) {
                SolveGoalAction solveGoalAction = new SolveGoalAction(box, goal);
                ideas.add(solveGoalAction);
                debug("yes! -> adding" + solveGoalAction.toString(),-2);
            } else {
                debug("no!",-2);
            }
        }
        if (DebugService.inDebugMode()) {
            String s = "Ideas conceived:";
            ArrayList<AbstractAction> actions = new ArrayList<>(ideas.getIdeas());
            for (AbstractAction action : actions) {
                s += "\n" + action.toString();
            }
            debug(s, -2);
        }
        BDIService.getInstance().getIdeas().put(goal.getLabel(), ideas);
        return ideas;
    }


    /**
     * This method tries to solve the level as best as possible at current state
     * @param target Goal to solve
     * @return
     */
    public PrimitivePlan solve(Goal target) {
        System.err.println("SOLVER is running - all levels should (ideally) be solved by this");
        Agent agent = BDIService.getInstance().getAgent();

        // update the meaning of this agent's life
        BDIService.getInstance().addMeaningOfLife(target);

        // Find the Ideas produced at bidding round for this goal
        Ideas ideas = BDIService.getInstance().getIdeas().get(target.getLabel());
        System.err.println("Ideas retrieved at solution round: "+ideas.toString());


        // TODO: Encapsulate the following in an AgentLevelPlanner...

        // Desire:  Find if possible a low level plan, and consider it a possible solution
        PrimitivePlan bestPlan = null;
        while (ideas.peekBest() != null) {
            SolveGoalAction bestIdea = ideas.getBest();
            PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
            // TODO: forward pls to state after currently executing plans

            HTNPlanner htnPlanner = new HTNPlanner(pls, bestIdea, RelaxationMode.NoAgents);
            PrimitivePlan realPlan = htnPlanner.plan();

            // update the best plan if the size of the non-relaxed plan is smaller
            if (realPlan != null) {
                if (bestPlan == null) {
                    bestPlan = realPlan;
                } else {
                    if (realPlan.size() < bestPlan.size()) {
                        bestPlan = realPlan;
                    }
                }
            }

            htnPlanner.reload(bestIdea, RelaxationMode.NoAgentsNoBoxes);
            PrimitivePlan relaxedPlan = htnPlanner.plan();

            PrimitivePlan currentPlan = null;

            if (relaxedPlan!=null) { // relaxed plan is usable and
                // TODO: Do high level planning (instead??)
                System.err.println("Implement HL Planning here");
                // create a HLPlan
                HLPlanner planner = new HLPlanner(bestIdea, pls);
                HLPlan hlPlan = planner.plan();

                // checkout the resulting HLPlan
                if (hlPlan!=null) {
                    System.err.println("Idea: " + bestIdea + " Agent " + agent + ": Found High Level Plan: " + hlPlan );
                } else {
                    System.err.println("Idea: " + bestIdea + " Agent " + agent + ": Did not find any High Level Plan." );
                }

                // Compare the length of the plans, and choose the shorter, (and evolve) and return it
                if ( bestPlan == null || hlPlan.approximateSteps(pls) < bestPlan.size() ) {
                    // evolve hlPlan to primitive plan
                    currentPlan = hlPlan.evolve(pls);

                    if (currentPlan != null) {
                        bestPlan = currentPlan;
                        // TODO: store the hlPlan in order for agent to react to changes later on (BDI v.4)
                        // BDIService.getInstance().getCurrentIntention().setHighLevelPlan(hlPlan);
                    }
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
            System.err.println("Agent " + agent + ": Found Concrete Plan: " + bestPlan.toString());
        } else {
            System.err.println("Agent " + agent + ": Did not find a Concrete Plan.");
            bestPlan = new PrimitivePlan();
        }

        // TODO: store the resulting plans and states in BDIService.getInstance() after planning..??

        return bestPlan;
    }


    /**
     * This method is more or less a training/test method
     * @param target
     * @return
     */
    public PrimitivePlan sandbox(Goal target) {
        System.err.println("SANDBOX is running - make sure that the level is SAD1");
        Box targetBox = new Box("A0");
        Agent agent = BDIService.getInstance().getAgent();

        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

        HMoveBoxAction mba1 = new HMoveBoxAction(targetBox, new Position(1,17), new Position(1,14));
        HTNPlanner htn1 = new HTNPlanner(pls, mba1, RelaxationMode.NoAgents);
        PrimitivePlan plan1 = htn1.plan();
        System.err.println("Plan 1:\n" + plan1);
        htn1.commitPlan(); // update positions in PlanningLevelService pls

        System.err.println("after plan 1 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba2 = new HMoveBoxAction(targetBox, new Position(2,7), new Position(1,17));
        HTNPlanner htn2 = new HTNPlanner(pls, mba2, RelaxationMode.NoAgents);
        PrimitivePlan plan2 = htn2.plan();
        System.err.println("Plan 2:\n" + plan2);
        htn2.commitPlan();

        System.err.println("after plan 2 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba3 = new HMoveBoxAction(targetBox, new Position(5,17), pls.getPosition(targetBox));
        HTNPlanner htn3 = new HTNPlanner(pls, mba3, RelaxationMode.NoAgents);
        PrimitivePlan plan3 = htn3.plan();
        System.err.println("Plan 3:\n" + plan3);
        htn2.commitPlan();

        System.err.println("after plan 3 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        plan1.appendActions(plan2);
        plan1.appendActions(plan3);
        System.err.println(plan1);

        return plan1;
    }






    /**
     * This method is more or less a training/test method
     * For testing purposes, like what happens if i simply task the agent with going 3 steps north...
     * @param target
     * @return
     */
    public PrimitivePlan clearPath(Goal target) {
        System.err.println("TEST 1 is running - make sure that the level is ClearPathTest.lvl");

        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

        HMoveBoxAction mba1 = new HMoveBoxAction(new Box("B1"), new Position(3,3), new Position(1,3));
        HMoveBoxAction mba2 = new HMoveBoxAction(new Box("A0"), new Position(1,5), new Position(1,4));

        HTNPlanner htn = new HTNPlanner(pls, mba1, RelaxationMode.NoAgents);
        PrimitivePlan plan1 = htn.plan();
        htn.commitPlan();

        htn.reload(mba2, RelaxationMode.NoAgents);
        PrimitivePlan plan2 = htn.plan();
        htn.commitPlan();

        PrimitivePlan plan = new PrimitivePlan();
        plan.appendActions(plan1);
        plan.appendActions(plan2);

        return plan;
    }


}

