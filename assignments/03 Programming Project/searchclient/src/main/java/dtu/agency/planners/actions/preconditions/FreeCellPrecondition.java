package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Position;

public class FreeCellPrecondition extends Precondition{

    private Position position;

    public FreeCellPrecondition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
