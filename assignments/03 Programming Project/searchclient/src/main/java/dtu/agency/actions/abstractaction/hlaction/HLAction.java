package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * High Level Action
 */
public abstract class HLAction extends AbstractAction {
    protected Agent agent = BDIService.getInstance().getAgent();

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
     * @param pls The PlanningLevelService supporting these operations
     * @return the approximated number of steps this HLAction is going to take
     */
    public abstract int approximateSteps(PlanningLevelService pls);

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


    public static HLAction cloneHLAction(HLAction other) {
        switch (other.getType()) {

            case RGotoAction:
                RGotoAction gta = (RGotoAction) other;
                return new RGotoAction(gta);

            case RMoveBoxAction:
                RMoveBoxAction rmba = (RMoveBoxAction) other;
                return new RMoveBoxAction(rmba);

            case No:
                NoAction na = (NoAction) other;
                return new NoAction(na);

            case HMoveBoxAndReturn:
                HMoveBoxAction hmba = (HMoveBoxAction) other;
                return new HMoveBoxAction(hmba);

            default:
                return null;
        }
    }

}
