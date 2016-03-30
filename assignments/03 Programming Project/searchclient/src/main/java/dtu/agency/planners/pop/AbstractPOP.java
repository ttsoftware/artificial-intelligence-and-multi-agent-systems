package dtu.agency.planners.pop;

import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.services.BoardObjectService;
import dtu.agency.services.LevelService;

import java.util.List;

public abstract class AbstractPOP<T extends AbstractAction> {

    protected BoardObjectService boardObjectService;
    protected Position agentStartPosition;
    protected Position boxStartPosition;
    protected Agent agent;

    public AbstractPOP(Agent agent) {
        this.agent = agent;
        this.agentStartPosition = LevelService.getInstance().getLevel()
                .getBoardObjectPositions().get(agent.getLabel());
        this.boardObjectService = new BoardObjectService(
                LevelService.getInstance().getLevel().getBoardObjects(),
                LevelService.getInstance().getLevel().getBoardState()
        );
    }

    public abstract POPPlan plan(T action);

    public abstract List<Precondition> getOpenPreconditions(List<Precondition> preconditions);

    protected int heuristic(Position agentPosition, Position goalPosition) {
        return Math.abs(agentPosition.getRow() - goalPosition.getRow()) + Math.abs(agentPosition.getColumn() - goalPosition.getColumn());
    }
}
