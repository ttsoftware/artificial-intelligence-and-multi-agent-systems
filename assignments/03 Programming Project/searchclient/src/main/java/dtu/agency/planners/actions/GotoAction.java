package dtu.agency.planners.actions;

import dtu.agency.board.Position;

public class GotoAction extends AbstractAction {

    private Position position;

    public GotoAction(Position position) {
        this.position = position;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.GotoAction;
    }

    public Position getPosition() {
        return position;
    }
}
