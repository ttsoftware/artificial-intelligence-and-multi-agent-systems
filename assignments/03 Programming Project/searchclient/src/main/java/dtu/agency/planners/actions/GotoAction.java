package dtu.agency.planners.actions;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.io.Serializable;
import java.util.*;

public class GotoAction extends HLAction implements Serializable {

    private final Position finalDestination;
    private final Box targetBox;

    public GotoAction(int row, int column) {
        this.finalDestination = new Position(row, column);
        this.targetBox = null;
    }

    public GotoAction(Position position) {
        this.finalDestination = position;
        this.targetBox = null;
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
    public boolean checkPreconditions(Level level, HTNEffect effect) {
        return false;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNEffect priorState, Level level) {
        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement;
        Action move;
        HTNEffect result;
        for (Direction dir : Direction.values()) {
            refinement = new MixedPlan();
            move = new MoveAction(dir);
            refinement.addAction(move);
            result = move.applyTo(priorState);
            // check if any of the resulting states fulfills this HLActions target,
            // and if so, return only the action which does!
            if (this.targetBox == null) { // no box, agent should end up at finalDestination
                if (!result.getAgentPosition().equals(this.finalDestination)) { //
                    refinement.addAction(this);
                }
            } else { // box target, agent should end up at neighbouring cell
                if (!result.getAgentPosition().isNeighbour(this.finalDestination)) { //
                    refinement.addAction(this);
                }
            } // if not, add this abstract action again

            if (result.isLegal(level)) {
                refinements.add(refinement);

            }
        }

        // else shuffle and return all refinements
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
