package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.agent.bdi.AgentBelief;
import dtu.agency.agent.bdi.AgentDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.planners.htn.HTNGoalPlanner;
import dtu.agency.planners.htn.HTNPlanner;

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
    private AgentBelief state;
    private AgentDesire primitivePlans;
    private static LinkedList<AgentIntention> intentions;
    private HashMap<String, HTNGoalPlanner> bids;          // everything the agent want to achieve (aka desires :-) )

    public BDIService(Agent bdiAgent) {
        agent = bdiAgent;

        state = new AgentBelief(agent);
        primitivePlans = new AgentDesire(new NoAction(state.getAgentCurrentPosition()) );
        intentions = new LinkedList<>();
        bids = new HashMap<>();

        Level levelClone = GlobalLevelService.getInstance().getLevel();

        // TODO: Remove agent from levelClone

        BDILevelService.getInstance().setLevel(levelClone);
    }

    public static Agent getAgent() {
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
