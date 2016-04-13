package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.NoAction;
import dtu.agency.agent.bdi.AgentBelief;
import dtu.agency.agent.bdi.AgentDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.planners.htn.HTNGoalPlanner;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Purpose of this BDIService is for the agent to be able to compare
 * own state to global, to enable it to interact by re-planning/communicating
 * in execution phase
 */
public class BDIService {

    private Agent agent;
    private AgentBelief state;
    private AgentDesire primitivePlans;
    private LinkedList<AgentIntention> intentions;
    private HashMap<String, HTNGoalPlanner> bids; // everything the agent want to achieve (aka desires :-) )
    private BDILevelService bdiLevelService;

    private static ThreadLocal<BDIService> threadLocal = new ThreadLocal<>();

    /**
     * We must call setInstance() before it becomes available
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
        state = new AgentBelief(agent);
        primitivePlans = new AgentDesire(new NoAction(state.getAgentCurrentPosition()) );
        intentions = new LinkedList<>();
        bids = new HashMap<>();

        Level levelClone = GlobalLevelService.getInstance().getLevel();

        // TODO: Remove agent from levelClone

        bdiLevelService = new BDILevelService(levelClone);
    }

    public Agent getAgent() {
        return agent;
    }

    public AgentBelief getState() {
        return state;
    }

    public AgentDesire getPrimitivePlans() {
        return primitivePlans;
    }

    public HashMap<String, HTNGoalPlanner> getBids() {
        return bids;
    }

    public LinkedList<AgentIntention> getIntentions() {
        return intentions;
    }

    public AgentIntention getCurrentIntention() {
        return intentions.getFirst();
    }

    public Box getCurrentTargetBox() {
        return getCurrentIntention().getHighLevelPlan().peek().getBox();
    }

    public void appendIntention(HLAction intention) {
        intentions.addLast(new AgentIntention(intention));
    }

    public BDILevelService getBDILevelService() {
        return bdiLevelService;
    }
}
