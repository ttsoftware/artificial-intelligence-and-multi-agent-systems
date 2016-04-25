package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.GlobalLevelService;

import java.util.HashMap;

public class ResolvedConflict {

    private HashMap<Integer, ConcretePlan> conflictPlans;

    public ResolvedConflict(Agent agentOne, ConcretePlan agentOnePlan, Agent agentTwo, ConcretePlan agentTwoPlan) {
        conflictPlans.put(Integer.valueOf(agentOne.getLabel()), agentOnePlan);
        conflictPlans.put(Integer.valueOf(agentTwo.getLabel()), agentTwoPlan);
    }

    public ResolvedConflict(Integer agentOne, ConcretePlan agentOnePlan, Integer agentTwo, ConcretePlan agentTwoPlan) {
        conflictPlans.put(agentOne, agentOnePlan);
        conflictPlans.put(agentTwo, agentTwoPlan);
    }

    public HashMap<Integer, ConcretePlan> getConflictPlans() {
        return conflictPlans;
    }
}
