package dtu.agency.planners.pop;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.actions.AbstractAction;
import dtu.agency.services.LevelService;

public abstract class AbstractPOP<T extends AbstractAction> {

    protected Position agentStartPosition;
    protected Agent agent;

    public AbstractPOP(Agent agent) {
        this.agent = agent;
        this.agentStartPosition = LevelService.getInstance().getLevel()
                .getBoardObjectPositions().get(agent.getLabel());
    }

    public abstract POPPlan plan(T action);
}
