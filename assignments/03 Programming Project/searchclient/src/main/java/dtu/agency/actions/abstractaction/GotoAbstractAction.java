package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Position;

public class GotoAbstractAction extends AbstractAction {

    private Position position;

    public GotoAbstractAction(Position position) {
        this.position = position;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.RGotoAction;
    }

    public Position getPosition() {
        return position;
    }
}
