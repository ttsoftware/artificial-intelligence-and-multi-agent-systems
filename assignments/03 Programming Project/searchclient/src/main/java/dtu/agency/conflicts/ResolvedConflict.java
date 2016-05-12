package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.services.GlobalLevelService;

import java.util.HashMap;

public class ResolvedConflict {

    private final Agent initiator;
    private final ConcretePlan initiatorPlan;
    private final Position initiatorPosition;
    private final Agent conceder;
    private final ConcretePlan concederPlan;
    private final Position concederPosition;

    private HashMap<Integer, ConcretePlan> conflictPlans = new HashMap<>();

    public ResolvedConflict(Agent initiator, ConcretePlan initiatorPlan, Position initiatorPosition, Agent conceder, ConcretePlan concederPlan, Position concederPosition) {
        this.initiator = initiator;
        this.initiatorPlan = initiatorPlan;
        this.initiatorPosition = initiatorPosition;
        this.conceder = conceder;
        this.concederPlan = concederPlan;
        this.concederPosition = concederPosition;

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
                if (initiatorPosition.equals(conflict.getInitiatorPosition())
                        && concederPosition.equals(conflict.getConcederPosition())) {
                    return true;
                }
            }

            if (conflict.getConceder().equals(initiator) && conflict.getInitiator().equals(conceder)) {
                if (concederPosition.equals(conflict.getInitiatorPosition())
                        && initiatorPosition.equals(getConcederPosition())) {
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

    public Position getInitiatorPosition() {
        return initiatorPosition;
    }

    public Position getConcederPosition() {
        return concederPosition;
    }
}
