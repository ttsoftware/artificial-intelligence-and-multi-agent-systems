package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.planners.plans.ConcretePlan;

import java.util.HashMap;

public class ResolvedConflict {

    private HashMap<Integer, ConcretePlan> conflictPlans = new HashMap<>();

    public ResolvedConflict(Agent agentOne, ConcretePlan agentOnePlan, Agent agentTwo, ConcretePlan agentTwoPlan) {
        conflictPlans.put(agentOne.getNumber(), agentOnePlan);
        conflictPlans.put(agentTwo.getNumber(), agentTwoPlan);
    }

    public ResolvedConflict(Integer agentOne, ConcretePlan agentOnePlan, Integer agentTwo, ConcretePlan agentTwoPlan) {
        conflictPlans.put(agentOne, agentOnePlan);
        conflictPlans.put(agentTwo, agentTwoPlan);
    }

    public HashMap<Integer, ConcretePlan> getConflictPlans() {
        return conflictPlans;
    }
}
