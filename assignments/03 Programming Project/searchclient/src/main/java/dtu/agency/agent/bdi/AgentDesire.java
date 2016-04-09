package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.planners.agentplanner.AgentPlan;
import dtu.agency.planners.htn.PrimitivePlan;

import java.util.LinkedList;

public class AgentDesire { // everything the agent might want to achieve
    private LinkedList<AgentPlan> desires;
    private int size;

    public AgentDesire() {
        desires = new LinkedList<>();
        size = 0;
    }

    public AgentPlan getNextDesire() {
        return desires.pollFirst();
    }
    public AgentPlan peekNextDesire() {
        return desires.peekFirst();
    }

    public void add(HLAction action, PrimitivePlan plan) {
        if (plan!=null) {
            desires.add( new AgentPlan(action, plan) );
            size += plan.size();
        }
    }

    public void add(AgentPlan agentPlan) {
        if (agentPlan!=null) {
            desires.add( agentPlan );
            size += agentPlan.getPlan().size();
        }
    }

    public int getSize() {
        return size;
    }
}
