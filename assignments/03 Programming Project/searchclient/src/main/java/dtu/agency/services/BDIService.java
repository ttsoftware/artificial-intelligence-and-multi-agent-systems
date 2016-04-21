package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.PrimitiveDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.*;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Purpose of this BDIService is for the agent to be able to compare
 * own state to global, to enable it to interact by re-planning/communicating
 * in execution phase
 */
public class BDIService {

    private Agent agent;
    private Box currentTargetBox; // used for saving box when planning!
    private Position agentCurrentPosition;
    private PrimitivePlan currentlyExecutingPlan = null;
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
        this.currentTargetBox = null;

        Level levelClone = GlobalLevelService.getInstance().getLevelClone();
        bdiLevelService = new BDILevelService(levelClone);

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

    public PrimitivePlan getCurrentlyExecutingPlan() {
        return currentlyExecutingPlan;
    }

    public void setCurrentlyExecutingPlan(PrimitivePlan currentlyExecutingPlan) {
        this.currentlyExecutingPlan = currentlyExecutingPlan;
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

    public Box getCurrentTargetBox() {
        return currentTargetBox;
    }

    public BDILevelService getBDILevelService() {
        return bdiLevelService;
    }

    public void setCurrentTargetBox(Box currentTargetBox) {
        this.currentTargetBox = currentTargetBox;
    }

    /**
     * TODO: This PLS should be changed to incluce the execution of the current intention
     * @return This PLS includes the execution of the current intention
     */
    public PlanningLevelService getLevelServiceAfterPendingPlans() {
        return new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
    }

    /**
     * @return the length of the current intended plans in number of steps
     */
    public int remainingConcreteActions(){
        if (currentlyExecutingPlan != null) {
            return currentlyExecutingPlan.size();
        }
        return 0;
    }
}
