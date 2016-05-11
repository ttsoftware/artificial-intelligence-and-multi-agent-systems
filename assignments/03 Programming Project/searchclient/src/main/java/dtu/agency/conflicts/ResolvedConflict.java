package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.GlobalLevelService;

import java.util.HashMap;

public class ResolvedConflict {

    public Agent initiator;
    public Agent conceder;
    public ConcretePlan initiatorPlan;
    public ConcretePlan concederPlan;

    private HashMap<Integer, ConcretePlan> conflictPlans = new HashMap<>();

    public ResolvedConflict(Agent initiator, ConcretePlan initiatorPlan, Agent conceder, ConcretePlan concederPlan) {
        this.initiator = initiator;
        this.conceder = conceder;
        this.initiatorPlan = initiatorPlan;
        this.concederPlan = concederPlan;

        conflictPlans.put(initiator.getNumber(), initiatorPlan);
        conflictPlans.put(conceder.getNumber(), concederPlan);
    }

    public HashMap<Integer, ConcretePlan> getConflictPlans() {
        return conflictPlans;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof ResolvedConflict) {
            ResolvedConflict conflict = (ResolvedConflict) object;


            // if you check for positions in the global level service it will always be equal.
            // have to save the position in the resolved conflicts.
            if (conflict.getInitiator().equals(initiator) && conflict.getConceder().equals(conceder)) {
                if (GlobalLevelService.getInstance().getPosition(initiator)
                        .equals(GlobalLevelService.getInstance().getPosition(conflict.getInitiator()))
                        && GlobalLevelService.getInstance().getPosition(conceder)
                        .equals(GlobalLevelService.getInstance().getPosition(conflict.getConceder()))
                        ) {
                    return true;
                }
            }

            if (conflict.getConceder().equals(initiator) && conflict.getInitiator().equals(conceder)) {
                if (GlobalLevelService.getInstance().getPosition(conceder)
                        .equals(GlobalLevelService.getInstance().getPosition(conflict.getInitiator()))
                        && GlobalLevelService.getInstance().getPosition(initiator)
                        .equals(GlobalLevelService.getInstance().getPosition(conflict.getConceder()))
                        ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (initiator.getNumber() > conceder.getNumber() ?
                String.format("%d%d", conceder.getNumber(), initiator.getNumber()) :
                String.format("%d%d", initiator.getNumber(), conceder.getNumber())).hashCode();
    }

    public Agent getInitiator() {
        return initiator;
    }

    public Agent getConceder() {
        return conceder;
    }

    public ConcretePlan getInitiatorPlan() {
        return initiatorPlan;
    }

    public ConcretePlan getConcederPlan() {
        return concederPlan;
    }
}
