package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
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
    // keeps track of Beliefs Desires and Intentions for an agent in 'real time'
    // THREAD LOCAL INSTANCE??
    private static Agent agent;
    private Position agentCurrentPosition;                   // agent's believed own position

    private BDILevelService levelService;
    private AgentDesire primitivePlans;
    private static LinkedList<AgentIntention> intentions;
    private HashMap<String, HTNGoalPlanner> bids;          // everything the agent want to achieve (aka desires :-) )

    public BDIService(Agent bdiAgent) {
        agent = bdiAgent;

        Level levelClone = GlobalLevelService.getInstance().getLevel();

        levelService = BDILevelService.getInstance();
        levelService.setLevel(levelClone);
        agentCurrentPosition = levelService.getPosition(agent);
        levelService.removeAgent(agent);

        primitivePlans = new AgentDesire(new NoAction(agentCurrentPosition));
        intentions = new LinkedList<>();
        bids = new HashMap<>();

    }

    public static Agent getAgent() {
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

    public static AgentIntention getCurrentIntention() {
        return intentions.getFirst();
    }

    public static Box getCurrentTargetBox() {
        return getCurrentIntention().getHighLevelPlan().peek().getBox();
    }

    public void appendIntention(HLAction intention) {
        intentions.addLast(new AgentIntention(intention));
    }
}
