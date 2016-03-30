package dtu.agency.planners.actions;

import dtu.agency.board.Position;

public abstract class AbstractAction {

    private Position position;

    public AbstractAction(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
