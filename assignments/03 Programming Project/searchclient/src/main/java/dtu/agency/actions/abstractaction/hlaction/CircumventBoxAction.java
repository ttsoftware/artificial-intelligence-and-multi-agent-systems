package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.MixedPlan;
import dtu.agency.services.LevelService;

import java.io.Serializable;
import java.util.ArrayList;

/*
* This Action tries to circumvent a Box in an open environment using just concrete 'move' actions
* THIS CLASS IS NOT FINISHED! NEEDS NEW REFINEMENTS! THINKING...
*/
public class CircumventBoxAction extends HLAction implements Serializable {

    private final Box box;
    private final Position agentDestination;

    public CircumventBoxAction(Box box, Position target) throws AssertionError {
        this.box = box;
        this.agentDestination = target;
        if (this.box == null || this.agentDestination == null) {
            throw new AssertionError("CircumventBoxAction: null values not accepted for box or agentDestination");
        }
    }

    public Box getBox() {
        return box;
    }

    public Position getAgentDestination() { return agentDestination; }

    @Override
    public Position getDestination() {
        return getAgentDestination();
    }

    @Override
    public boolean isPureHLAction() { return false; }

    @Override
    public boolean isPurposeFulfilled(HTNState htnState) {
        boolean fulfilled = htnState.getAgentPosition().equals( getAgentDestination() );
        fulfilled &= htnState.getBoxPosition().equals( LevelService.getInstance().getPosition(box) );
        return fulfilled;
    }

    @Override
    public ArrayList<MixedPlan> getRefinements(HTNState priorState) {
        // check if the prior state fulfills this HLActions agentDestination, and if so return empty plan of refinements
        // System.err.println("CBA.getRefinements - Initial" + priorState.toString());
        if (isPurposeFulfilled(priorState)) return doneRefinement();

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        MixedPlan refinement = new MixedPlan();
        refinement.addAction(new GotoAction( getAgentDestination() ) );
        refinements.add(refinement);
        //System.err.println("CBA.getRefine(): "+ refinements.toString());
        return refinements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("CircumventBoxAction(");
        s.append(getBox().toString());
        s.append(",");
        s.append(getAgentDestination().toString());
        s.append(")");
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CircumventBoxAction other = (CircumventBoxAction) obj;
        if (!this.getBox().equals(other.getBox()))
            return false;
        if (!this.getAgentDestination().equals(other.getAgentDestination()))
            return false;
        return true;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.Circumvent;
    }
}
