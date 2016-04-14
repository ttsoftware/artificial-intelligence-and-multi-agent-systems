package dtu.agency.actions.abstractaction.rlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;

public class RMoveBoxAction extends RLAction {

    private final Box box;
    private final Position boxDestination;

    public RMoveBoxAction(Box box, Position boxDestination) {
        this.box = box;
        this.boxDestination = boxDestination;
    }

    public RMoveBoxAction(RMoveBoxAction other) {
        this.box = new Box(other.getBox());
        this.boxDestination = new Position(other.getAgentDestination());
    }

    @Override
    public AbstractActionType getType() {
    return AbstractActionType.RMoveBoxAction;
    }

    @Override
    public Position getAgentDestination() {
        return boxDestination;
    }

    @Override
    public Position getBoxDestination() {
        return boxDestination;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        String s = "RMoveBoxAction(" +
                getBox().toString() +
                "->" +
                boxDestination.toString() +
                ")";
        return s;
    }

    @Override
    public int approximateSteps(Position agentOrigin) {
        int approximateSteps = 0;
        // TODO PlanningLevelService??
        Position boxOrigin = BDIService.getInstance().getBDILevelService().getPosition(box);
        approximateSteps += boxOrigin.manhattanDist(boxDestination);
        return approximateSteps;
    }
}
