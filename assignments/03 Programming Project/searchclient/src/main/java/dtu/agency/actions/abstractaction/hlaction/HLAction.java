package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.MixedPlan;

import java.util.ArrayList;

/**
 * High Level Action
 */
public abstract class HLAction extends AbstractAction {

    /**
     * This is where the agent is going to be after completing this action
     */
    public abstract Position getAgentDestination();

    /**
     * This is where the box is going to be after completing this action
     */
    public abstract Position getBoxDestination();

    /**
     * This is the targeted box during this action
     */
    public abstract Box getBox();

    @Override
    public abstract String toString();

    /**
     *
     * @param agentOrigin
     * @return the approximated number of steps this HLAction is going to take
     */
    public abstract int approximateSteps(Position agentOrigin);

    /**
     * Creates an empty refinement of known signature,
     * to return if sub goal is completed
    */
    public ArrayList<MixedPlan> doneRefinement() {
        ArrayList<MixedPlan> refinements = new ArrayList<>();
        MixedPlan refinement = new MixedPlan();
        refinement.addAction(new NoConcreteAction());
        refinements.add(refinement);
        return refinements;
    }


    public static HLAction getOriginalAction(HLAction originalAction) {
        switch (originalAction.getType()) {
            case SolveGoal:
                SolveGoalAction sga = (SolveGoalAction) originalAction;
                return new SolveGoalAction(sga);

            case RGotoAction:
                RGotoAction gta = (RGotoAction) originalAction;
                return new RGotoAction(gta);

            case MoveBoxAction:
                RMoveBoxAction rmba = (RMoveBoxAction) originalAction;
                return new RMoveBoxAction(rmba);

            case No:
                NoAction na = (NoAction) originalAction;
                return new NoAction(na);

            case MoveBoxAndReturn:
                HMoveBoxAction hmba = (HMoveBoxAction) originalAction;
                return new HMoveBoxAction(hmba);

            default:
                return null;
        }
    }

}
