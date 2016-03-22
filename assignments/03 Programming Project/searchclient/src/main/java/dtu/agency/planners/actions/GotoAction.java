package dtu.agency.planners.actions;

import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

import java.io.Serializable;
import java.util.*;

public class GotoAction extends HLAction implements Serializable {

    private Position finalDestination;
    private Box targetBox = null;

    public GotoAction(int row, int column) {
        this.finalDestination = new Position(row, column);
    }

    public GotoAction(Box box) {
        this.finalDestination = box.getPosition();
        this.targetBox = box;
    }

    public Position getDestination() {
        return this.finalDestination;
    }

    public Box getTargetBox() {
        return targetBox;
    }

    @Override
    public List<List<AbstractAction>> getRefinements() {
        List<List<AbstractAction>> refinements = new ArrayList<>();

        List<AbstractAction> refinement_1 = new LinkedList<>();
        refinement_1.add(new MoveAction(Direction.NORTH) );
        refinement_1.add(this);
        refinements.add(refinement_1);

        long seed = System.nanoTime();
        Collections.shuffle(refinements, new Random(seed));

        return refinements;
    }

    public boolean equals(GotoAction o) {
        if (this.getTargetBox().equals(o.getTargetBox()))
            if (this.getDestination().equals(o.getDestination())) return true;
        return false;
    }
}
