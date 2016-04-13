package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.NoAction;
import dtu.agency.agent.bdi.AgentDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
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
    private Position agentCurrentPosition;
    private AgentDesire primitivePlans;
    private LinkedList<AgentIntention> intentions;
    private HashMap<String, HTNGoalPlanner> bids; // everything the agent want to achieve (aka desires :-) )
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
        System.err.println(Thread.currentThread().getName() + ": Creating BDIService for agent: " + agent.getLabel());

        this.agent = agent;
        Level levelClone = GlobalLevelService.getInstance().getLevelClone();
        bdiLevelService = new BDILevelService(levelClone);

        agentCurrentPosition = bdiLevelService.getPosition(this.agent);
        System.err.println("Agents" + bdiLevelService.getLevel().getAgents().toString());
        bdiLevelService.removeAgent(this.agent);
        System.err.println("Agents" + bdiLevelService.getLevel().getAgents().toString());
        bdiLevelService.insertAgent(this.agent, agentCurrentPosition);
        System.err.println("Agents" + bdiLevelService.getLevel().getAgents().toString());

        primitivePlans = new AgentDesire(new NoAction(agentCurrentPosition));
        intentions = new LinkedList<>();
        bids = new HashMap<>();
    }

    public Agent getAgent() {
        return agent;
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
