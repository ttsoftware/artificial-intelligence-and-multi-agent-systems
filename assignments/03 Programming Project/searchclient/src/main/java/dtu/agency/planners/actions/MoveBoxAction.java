package dtu.agency.planners.actions;

import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.HTNEffect;

import java.util.*;

public class MoveBoxAction extends HLAction {

    private final Position finalDestination;
    private final Box targetBox;
    private final Goal targetGoal;


    public MoveBoxAction(Box box, Position target) {
        this.finalDestination = target;
        this.targetBox = box;
        this.targetGoal = null;
    }

    public MoveBoxAction(Box box, Goal goal) {
        this.finalDestination = goal.getPosition();
        this.targetBox = box;
        this.targetGoal = goal;
    }

    public Box getBox() {
        return targetBox;
    }

    public Position getDestination() {
        return finalDestination;
    }

    public Goal getGoal() {
        return targetGoal;
    }

    @Override
    public boolean checkPreconditions(Level level, HTNEffect effect) {
        return false;
    }

    @Override
    public ArrayList<LinkedList<AbstractAction>> getRefinements(Direction dirToBox) {
        ArrayList<LinkedList<AbstractAction>> refinements = new ArrayList<>();

        // then can we check if push/pull  direction os valid before adding it??
        // else leave it for someone else

        LinkedList<AbstractAction> refinement_1 = new LinkedList<>();
        LinkedList<AbstractAction> refinement_2 = new LinkedList<>();
        // do this 3 more times


        refinement_1.add(new PushAction(targetBox, Direction.NORTH, dirToBox) );
        refinement_1.add(this);

        refinement_2.add(new PullAction(targetBox, Direction.NORTH, dirToBox) );
        refinement_2.add(this);

        refinements.add(refinement_1);
        refinements.add(refinement_2);

        long seed = System.nanoTime();
        Collections.shuffle(refinements, new Random(seed));

        return refinements;
    }

}
