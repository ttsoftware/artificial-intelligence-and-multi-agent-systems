package dtu.agency.planners.actions;

import dtu.agency.board.Position;

public class GotoAbstractAction extends AbstractAction {

    private Position position;

    public GotoAbstractAction(int estimatedDistance, Position position) {
        this.position = position;
        this.estimatedDistance = estimatedDistance;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.GotoAction;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int getEstimatedDistance() {
        return estimatedDistance;
    }
}
