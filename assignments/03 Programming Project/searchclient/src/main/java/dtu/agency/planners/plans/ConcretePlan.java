package dtu.agency.planners.plans;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Position;

import java.util.Collection;

public interface ConcretePlan extends Plan<ConcreteAction> {
    Collection<ConcreteAction> getActions();
    ConcreteAction popAction();
    public int approximateSteps(Position agentInitialPosition);
    }
