package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.planners.htn.HTNState;

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
     * This is the targeted box during this action(s)
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

}
