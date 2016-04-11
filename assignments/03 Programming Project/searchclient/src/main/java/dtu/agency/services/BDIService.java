package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.agent.bdi.AgentBelief;
import dtu.agency.agent.bdi.AgentDesire;
import dtu.agency.agent.bdi.AgentIntention;
import dtu.agency.board.Agent;
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
    private Agent agent;
    private AgentBelief state;
    private AgentDesire primitivePlans;
    private LinkedList<AgentIntention> intentions;
    private HashMap<String, HTNPlanner> bids;          // everything the agent want to achieve (aka desires :-) )

    public BDIService(Agent agent) {
        this.agent = agent;
        state = new AgentBelief(agent);
        primitivePlans = new AgentDesire(new NoAction(state.getAgentCurrentPosition()) );
        intentions = new LinkedList<>();
        bids = new HashMap<>();
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

    public HashMap<String, HTNPlanner> getBids() {
        return bids;
    }

    public LinkedList<AgentIntention> getIntentions() {
        return intentions;
    }

    public AgentIntention getCurrentIntention() {
        return intentions.getFirst();
    }

    public void appendIntention(HLAction intention) {
        intentions.addLast(new AgentIntention(intention));
    }

/*
    public boolean planIsSound(HLPlan plan, AgentIntention intention, AgentBelief belief) {
        HLAction intent;
        if (plan.getIntention() != intention.getCurrentIntention()) {
            return false;
        } else {
            intent = plan.getIntention();
        }

        switch (intention.getCurrentIntention().getType()) {
            case SolveGoal:
                SolveGoalAction sga = (SolveGoalAction) intent;
                break;
            case Circumvent:
                CircumventBoxAction cba = (CircumventBoxAction) intent;
                break;
            case RGotoAction:
                RGotoAction gta = (RGotoAction) intent;
                break;
            case RMoveBoxAction:
                RMoveBoxAction mba = (RMoveBoxAction) intent;
                break;
            case MoveBoxAndReturn:
                HMoveBoxAction mbar = (HMoveBoxAction) intent;
                break;
        }
        return true;
    }
*/

}
