package dtu.agency.planners;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.agent.bdi.AgentIntention;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

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
            // TODO: check for colors
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
        return ideas;
    }


    /**
     * Select the best idea from the top five ideas, and evolve it into a desire
     */
    public AgentIntention filter( Ideas ideas, Goal goal) { // Belief is handled internally by pls
        AgentIntention bestIntention = null;
        int bestApproximation = Integer.MAX_VALUE;
        int counter = (ideas.getIdeas().size() < 5) ? ideas.getIdeas().size() : 5 ;

        while ( counter > 0 ) {
            counter--;
            SolveGoalAction idea = ideas.getBest();
            Box targetBox = idea.getBox();
            Position targetBoxPosition = pls.getPosition(targetBox);

            HTNPlanner htn = new HTNPlanner(pls, idea, RelaxationMode.NoAgentsNoBoxes);
            PrimitivePlan pseudoPlan = htn.plan();
            if (pseudoPlan==null) {continue;}
            // the positions of the cells that the agent is going to step on top of
            LinkedList<Position> pseudoPath = pls.getOrderedPath(pseudoPlan);

            LinkedList<Position> obstaclePositions = pls.getObstaclePositions(pseudoPath);

            // see how many obstacles on the path are reachable (cheaper) / unreachable (more expensive)
            int nReachable = 0;
            ListIterator iterator = obstaclePositions.listIterator(0);

            while (iterator.hasNext()){
                Position next = (Position) iterator.next();
                nReachable++;
                if (targetBoxPosition.equals(next)) {
                    break;
                }
            }

            obstaclePositions.contains(pls.getPosition(targetBox));

            int nUnReachable = obstaclePositions.size() - nReachable;

            AgentIntention intention = new AgentIntention(goal, targetBox, pseudoPlan, pseudoPath, obstaclePositions, nReachable, nUnReachable);
            if (intention.getApproximateSteps() < bestApproximation) {
                bestIntention = intention;
                bestApproximation = intention.getApproximateSteps();
            }

        }

        BDIService.getInstance().getIntentions().put(goal.getLabel(), bestIntention);
        return bestIntention;
    }

    /**
     * This method tries to solve the level as best as possible at current state
     * @param target Goal to solve
     * @return The primitive plan solving this goal
     */
    public PrimitivePlan solve(Goal target) {

        debug("SOLVER is running - all levels should (ideally) be solved by this",2);

        // Find the Ideas produced at bidding round for this goal
        AgentIntention intention = BDIService.getInstance().getIntentions().get(target.getLabel());
        debug("Intention retrieved at solution round: "+intention);

        HLPlanner planner = new HLPlanner(intention, pls);
        HLPlan hlPlan = planner.plan();

        PrimitivePlan plan = null;
        if (hlPlan != null) {
            // TODO: store the hlPlan in order for agent to react to changes later on (BDI v.4)
            // BDIService.getInstance().getCurrentIntention().setHighLevelPlan(hlPlan);
            plan = hlPlan.evolve(pls);
        } else {
            // TODO: failed what to do...
        }

        // Check the result of this planning phase
        if (plan != null) {
            debug("Agent " + agent + ": Found Concrete Plan: " + plan.toString());
        } else {
            debug("Agent " + agent + ": Did not find a Concrete Plan.");
        }

        debug("",-2);
        return plan;
    }


    /**
     * This method is more or less a training/test method
     * @return The primitive plan solving this goal
     */
    public PrimitivePlan sandbox() {
        debug("SANDBOX is running - make sure that the level is SAD1");
        Box targetBox = new Box("A0");
        Agent agent = BDIService.getInstance().getAgent();

        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());

        HMoveBoxAction mba1 = new HMoveBoxAction(targetBox, new Position(1,17), new Position(1,14));
        HTNPlanner htn1 = new HTNPlanner(pls, mba1, RelaxationMode.NoAgents);
        PrimitivePlan plan1 = htn1.plan();
        debug("Plan 1:\n" + plan1);
        htn1.commitPlan(); // update positions in PlanningLevelService pls

        debug("after plan 1 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba2 = new HMoveBoxAction(targetBox, new Position(2,7), new Position(1,17));
        HTNPlanner htn2 = new HTNPlanner(pls, mba2, RelaxationMode.NoAgents);
        PrimitivePlan plan2 = htn2.plan();
        debug("Plan 2:\n" + plan2);
        htn2.commitPlan();

        debug("after plan 2 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        HMoveBoxAction mba3 = new HMoveBoxAction(targetBox, new Position(5,17), pls.getPosition(targetBox));
        HTNPlanner htn3 = new HTNPlanner(pls, mba3, RelaxationMode.NoAgents);
        PrimitivePlan plan3 = htn3.plan();
        debug("Plan 3:\n" + plan3);
        htn2.commitPlan();

        debug("after plan 3 - pls: agent:"+pls.getPosition(agent)+ " Box:" +pls.getPosition(targetBox) );

        plan1.appendActions(plan2);
        plan1.appendActions(plan3);
        debug("" + plan1);

        return plan1;
    }






    /**
     * This method is more or less a training/test method
     * For testing purposes, like what happens if i simply task the agent with going 3 steps north...
     * @param target Goal to solve
     * @return The primitive plan solving this goal
     */
    public PrimitivePlan clearPath(Goal target) {
        debug("TEST 1 is running - make sure that the level is ClearPathTest.lvl");

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

