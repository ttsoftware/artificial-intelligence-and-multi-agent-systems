package dtu.agency.planners.plans;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Position;

import java.util.List;

public interface ConcretePlan extends Plan<ConcreteAction> {
    List<ConcreteAction> getActions();
    ConcreteAction popAction();
    public int approximateSteps(Position agentInitialPosition);
    }
