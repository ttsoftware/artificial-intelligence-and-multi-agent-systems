package dtu.agency.planners.actions;

import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MoveBoxAction extends HLAction {

    private final Position finalDestination;
    private final Box targetBox;
    private final Goal targetGoal;


    public MoveBoxAction(Agent agent, Box box, Goal goal) {
        this.finalDestination = box.getPosition();
        this.targetBox = box;
        this.targetGoal = goal;
    }

    @Override
    public List<List<AbstractAction>> getRefinements() {
        List<List<AbstractAction>> refinements = new ArrayList<LinkedList<AbstractAction>>();

        // first, find the direction that the target box is
        Direction boxDirection = getBoxDirection(Effect);

        // then can we check if push/pull  direction os valid before adding it??
        // else leave it for someone else

        List<HLAction> refinement_1 = new LinkedList<HLAction>();
        List<HLAction> refinement_2 = new LinkedList<HLAction>();
        // do this 3 more times


        refinement_1.add(new PushAction(targetBox, Direction.NORTH, boxDirection) );
        refinement_1.add(this);

        refinement_2.add(new PullAction(targetBox, Direction.NORTH, boxDirection) );
        refinement_2.add(this);

        refinements.add(refinement_1);
        refinements.add(refinement_2);
        refinements.add(refinement_3);
        refinements.add(refinement_4);

        long seed = System.nanoTime();
        Collections.shuffle(refinements, new Random(seed));

        return refinements;
    }

}
