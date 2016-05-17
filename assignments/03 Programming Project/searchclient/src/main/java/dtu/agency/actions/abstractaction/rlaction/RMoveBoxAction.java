package dtu.agency.actions.abstractaction.rlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

public class RMoveBoxAction extends RLAction {

    private final Box box;
    private final Position boxDestination;

    public RMoveBoxAction(Box box, Position boxDestination) {
        this.box = box;
        this.boxDestination = boxDestination;
        if (!(box != null && boxDestination != null)) throw new AssertionError("RMoveBoxAction box or destination null at init");
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
        return "RMoveBoxAction(" + getBox().toString() + "->" + boxDestination.toString() + ")";
    }

    @Override
    public int approximateSteps(PlanningLevelService pls) {
        return pls.getPosition(box).manhattanDist(boxDestination) - 1;
    }
}
