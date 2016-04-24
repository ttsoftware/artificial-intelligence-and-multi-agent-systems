package dtu.agency.services;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.*;
import dtu.agency.planners.hlplanner.HLPlanner;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Purpose of this BDIService is for the agent to be able to compare
 * own state to global, to enable it to interact by re-planning/communicating
 * in execution phase
 */
public class BDIService {
    private static void debug(String msg, int indentationChange) {
        DebugService.print(msg, indentationChange);
    }
    private static void debug(String msg) {
        debug(msg, 0);
    }
//    DebugService.setDebugLevel(DebugService.DebugLevel.PICKED); // ***     DEBUGGING LEVEL     ***
//    boolean oldDebugMode = DebugService.setDebugMode(true);     // *** START DEBUGGER MESSAGES ***
//    DebugService.setDebugMode(oldDebugMode);                    // ***  END DEBUGGER MESSAGES  ***

    private Agent agent;
    private HLPlan planToBeExecuted;
    private PrimitivePlan stepsToBeExecuted;
    private LinkedList<Goal> goalsToSolve;
    private HashMap<String, AgentIntention> intentions;
    private BDILevelService bdiLevelService;

    private static ThreadLocal<BDIService> threadLocal = new ThreadLocal<>();

    /**
     * We must call setInstance() before it becomes available
     *
     * @param bdiService
     */
    public static void setInstance(BDIService bdiService) {
        threadLocal.set(bdiService);
    }

    public static BDIService getInstance() {
        return threadLocal.get();
    }

    public BDIService(Agent agent) {
        this.agent = agent;

        Level levelClone = GlobalLevelService.getInstance().getLevelClone();
        bdiLevelService = new BDILevelService(levelClone);

        planToBeExecuted = new HLPlan();
        stepsToBeExecuted = new PrimitivePlan();
        goalsToSolve = new LinkedList<>();
        intentions = new HashMap<>();
    }

    /**
     * Interface actions  /  API to an agent's mind
     * These actions return an actual answer
     */

    /**
     * @return Who do I think I am
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * @return Where do I Think I am
     */
    public Position getAgentCurrentPosition() {
        return bdiLevelService.getPosition(agent);
    }

    /**
     * @return What am I going to do next
     */
    public PrimitivePlan getStepsToBeExecuted() {
        if (stepsToBeExecuted.isEmpty()) {
            calculateNextSteps();
        }
        return new PrimitivePlan(stepsToBeExecuted);
    }

    /**
     * @return What do I think my purpose is in life
     */
    public LinkedList<Goal> getGoalsToSolve() {
        return goalsToSolve;
    }

    /**
     * @return My current perception of the environment/level surrounding me, in it's current state
     */
    public BDILevelService getBDILevelService() {
        return bdiLevelService;
    }

    /**
     * @return My perception of how the environment surrounding me will look after I have executed my current plans
     */
    public PlanningLevelService getLevelServiceAfterPendingPlans() {
        // TODO: This PLS should be changed to incluce the execution of the current intention
        PlanningLevelService pls = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevelClone());
        HLPlan tempHLPlan = new HLPlan(planToBeExecuted);
        PrimitivePlan tempLLPlan = new PrimitivePlan(stepsToBeExecuted);
        System.err.println("getLevelServiceAfterPendingActions(): show pending actions");
        System.err.println(""+ tempHLPlan);
        System.err.println(""+ tempLLPlan);
//        pls.applyAll(tempLLPlan);
//        pls.applyAll(tempHLPlan);
        return pls;
    }

    /**
     * @param goal The goal solved by this intention
     * @return My previously chosen intention, concerning this goal, thought of at a prior state
     */
    public AgentIntention getIntention(Goal goal) {
        return intentions.get(goal.getLabel());
    }

    /**
     * @return How many steps do I currently think i have to execute
     */
    public int remainingConcreteActions(){
        if (stepsToBeExecuted != null) {
            return stepsToBeExecuted.size();
        }
        return 0;
    }


    /**
     * Public access to my thought routines,
     * some are just changing my inner state, but not returning anything
     * while other are more sporadic thoughts returning stuff that is not stored
     */


    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     *
     * @param goal The goal targeted
     * @return The ideas (list of SolveGoalActions) that could potentially solve this goal.
     */
    public Ideas thinkOfIdeas(Goal goal) {
        debug("getIdeas(): ", 2);
        PlanningLevelService pls = new PlanningLevelService(getLevelServiceAfterPendingPlans());
        Ideas ideas = new Ideas(goal, pls); // agent destination is used for heuristic purpose

        for (Box box : pls.getLevel().getBoxes()) {
            // TODO: check for colors
            debug(box.getLabel().substring(0, 1).toLowerCase() + "=?" + goal.getLabel().toLowerCase().substring(0, 1), 2);
            if (box.getLabel().toLowerCase().substring(0, 1).equals(goal.getLabel().toLowerCase().substring(0, 1))) {
                SolveGoalAction solveGoalAction = new SolveGoalAction(box, goal);
                ideas.add(solveGoalAction);
                debug("yes! -> adding" + solveGoalAction.toString(), -2);
            } else {
                debug("no!", -2);
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
    public boolean filterIdeas(Ideas ideas, Goal goal ) { // Belief is handled internally by pls
        PlanningLevelService pls = new PlanningLevelService(getLevelServiceAfterPendingPlans());
        AgentIntention bestIntention = null;
        int bestApproximation = Integer.MAX_VALUE;
        int counter = (ideas.getIdeas().size() < 5) ? ideas.getIdeas().size() : 5 ;

        // plan only 5 best initial heuristics, but if none of those are valid -> keep planning.
        while ( counter > 0 || bestIntention == null) {
            counter--;
            if (ideas.getIdeas().isEmpty()) break;
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
        if (bestIntention != null) {
            getIntentions().put(goal.getLabel(), bestIntention);
            return true;
        }
        return false;
    }


    /**
     * This method tries to solve the level as best as possible at current state
     *
     * @param goal The goal to be solved
     * @return The success whether a plan was found, to solving this goal
     */
    public boolean solveGoal(Goal goal) {
        debug("SolveGoal is running - all levels should (ideally) be solved by this", 2);
        // update the meaning of this agent's life
        addToMeaningOfLife(goal);

        // Continue solving this goal, using the Intention found in the bidding round
        AgentIntention intention = getIntentions().get(goal.getLabel());
        PlanningLevelService pls = new PlanningLevelService(getLevelServiceAfterPendingPlans());

        HLPlanner planner = new HLPlanner(intention, pls);
        HLPlan hlPlan = planner.plan();

        // Check the result of this planning phase, and return success
        if (hlPlan != null) {
            debug("Agent " + agent + ": Found HighLevel Plan: " + hlPlan.toString(), -2);
            planToBeExecuted.extend(hlPlan);
            return true;
        } else {
            debug("Agent " + agent + ": Did not find a HighLevel Plan.", -2);
            return false;
        }
    }

    /**
     * Convert my entire set of remaining High Level Plans to Concrete Actions,
     * and append them to my current Low Level Plan
     */
    public void calculateNextSteps() {
        // TODO going from BDI v.3 --> BDI v.4 (REACTIVE AGENT)
        // are we gonna submit the entire primitivePlan to the agency at once??
        // maybe it is better to divide the sending of plans into smaller packages,
        // e.g. solving separate intentions as GotoBox, MoveBox, etc.
        // this will give the agent the possibility of reacting to changes
        // in the environment.

        PlanningLevelService pls = new PlanningLevelService(getLevelServiceAfterPendingPlans());
        stepsToBeExecuted.appendActions( planToBeExecuted.evolve(pls) );
        planToBeExecuted.getActions().clear();
    }


    /**
     * Private thought routines - Memory I/O, etc.
     */

    /**
     * @param target add purpose to my life
     */
    private void addToMeaningOfLife(Goal target) {
        goalsToSolve.addLast(target);
    }

    /**
     * @return Retrieve memory of Intentions, given a goal..
     */
    private HashMap<String, AgentIntention> getIntentions() {
        return intentions;
    }

}
