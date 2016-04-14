package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.PrimitiveDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.*;

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
    private PrimitiveDesire primitivePlans;
    private LinkedList<Goal> meaningOfLife;
    private LinkedList<AgentIntention> intentions;
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

        // TODO: make this a unit test instead!
        if (bdiLevelService.getLevel().getBoardObjectPositions()
                == GlobalLevelService.getInstance().getLevel().getBoardObjectPositions()) {
            try {
                throw new Exception("We must deepcopy");
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        primitivePlans = new PrimitiveDesire(null);
        meaningOfLife = new LinkedList<>();
        intentions = new LinkedList<>();
        ideas = new HashMap<>();
    }

    public Agent getAgent() {
        return agent;
    }

    public Position getAgentCurrentPosition() {
        return bdiLevelService.getPosition(agent);
    }

    public PrimitiveDesire getPrimitivePlans() {
        return primitivePlans;
    }

    public HashMap<String, Ideas> getIdeas() {
        return ideas;
    }

    public void addMeaningOfLife(Goal target) {
        meaningOfLife.addLast(target);
    }

    public LinkedList<Goal> getMeaningOfLife() {
        return meaningOfLife;
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
