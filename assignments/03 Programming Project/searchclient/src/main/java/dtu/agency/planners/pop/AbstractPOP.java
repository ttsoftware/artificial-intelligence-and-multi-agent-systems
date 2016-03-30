package dtu.agency.planners.pop;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.services.BoardObjectService;
import dtu.agency.services.LevelService;

public abstract class AbstractPOP<T extends AbstractAction> {

    private BoardObjectService boardObjectService;
    private Position agentStartPosition;
    private Position boxStartPosition;
    private Agent agent;

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

    private int heuristic(Position agentPosition, Position goalPosition) {
        return Math.abs(agentPosition.getRow() - goalPosition.getRow()) + Math.abs(agentPosition.getColumn() - goalPosition.getColumn());
    }
}
