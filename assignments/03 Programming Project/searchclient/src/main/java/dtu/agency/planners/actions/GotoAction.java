package dtu.agency.planners.actions;

import dtu.agency.board.Box;
import dtu.agency.board.Position;

import java.util.Hashtable;

public class GotoAction extends AbstractAction {

    private Position position;

    public GotoAction(Position position) {
        this.position = position;
    }

    public GotoAction(Box box, Hashtable<String, Position> boardObjectPositions) {
        this.position = boardObjectPositions.get(box.getLabel());
    }

    public Position getPosition() {
        return this.position;
    }
}
