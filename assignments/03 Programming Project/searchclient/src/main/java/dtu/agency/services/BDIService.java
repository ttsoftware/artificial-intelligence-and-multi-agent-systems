package dtu.agency.services;

import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.PrimitiveDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.*;
import dtu.agency.planners.hlplanner.HLPlanner;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.HashMap;
import java.util.LinkedList;

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
    private PrimitiveDesire primitivePlans;
    private LinkedList<Goal> goalsToSolve;
    private HashMap<String, AgentIntention> intentions;
    private HashMap<String, Ideas> ideas; // everything the agent want to achieve (aka desires :-) )
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
        primitivePlans = new PrimitiveDesire(null);
        goalsToSolve = new LinkedList<>();
        intentions = new HashMap<>();
        ideas = new HashMap<>();
    }

    public Agent getAgent() {
        return agent;
    }

    public Position getAgentCurrentPosition() {
        return bdiLevelService.getPosition(agent);
    }

    public PrimitivePlan getStepsToBeExecuted() {
        return stepsToBeExecuted;
    }

    public void setStepsToBeExecuted(PrimitivePlan stepsToBeExecuted) {
        this.stepsToBeExecuted = stepsToBeExecuted;
    }

    public PrimitiveDesire getPrimitivePlans() {
        return primitivePlans;
    }

    public HashMap<String, Ideas> getIdeas() {
        return ideas;
    }

    public void addMeaningOfLife(Goal target) {
        goalsToSolve.addLast(target);
    }

    public LinkedList<Goal> getGoalsToSolve() {
        return goalsToSolve;
    }

    public HashMap<String, AgentIntention> getIntentions() {
        return intentions;
    }

    public BDILevelService getBDILevelService() {
        return bdiLevelService;
    }

    /**
     * TODO: This PLS should be changed to incluce the execution of the current intention
     * @return This PLS includes the execution of the current intention
     */
    public PlanningLevelService getLevelServiceAfterPendingPlans() {
        return new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevelClone());
    }

    /**
     * @return the length of the current intended plans in number of steps
     */
    public int remainingConcreteActions(){
        if (stepsToBeExecuted != null) {
            return stepsToBeExecuted.size();
        }
        return 0;
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
        addMeaningOfLife(goal);

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


    public PrimitivePlan calculateNextSteps() {
        PlanningLevelService pls = new PlanningLevelService(getLevelServiceAfterPendingPlans());
        stepsToBeExecuted.appendActions( planToBeExecuted.evolve(pls) );
        planToBeExecuted.getActions().clear();
        return stepsToBeExecuted;
    }
}
