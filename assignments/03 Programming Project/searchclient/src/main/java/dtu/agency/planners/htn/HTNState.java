package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.services.DebugService;
import dtu.agency.services.GlobalLevelService;

public class HTNState {
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }

    private final Position agentPosition;
    private final Position boxPosition;

    public HTNState(Position agentPosition, Position boxPosition) {
        this.agentPosition = agentPosition;
        this.boxPosition = boxPosition;
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return GlobalLevelService.getInstance().getRelativeDirection(agentPosition, boxPosition, false);
    }

    public boolean boxIsMovable() {
        return agentPosition.isAdjacentTo(boxPosition);
    }

    /**
     * Refactored this, such that the action does not apply a state, but a state applies an action.
     * The given instance is the "oldState", and the returned state is the "newState"
     *
     * @param concreteAction
     * @return a new HTNState instance with @concreteAction applied to it
     */
    public HTNState applyConcreteAction(ConcreteAction concreteAction) {
        debug("applyConcreteActions():", 2);
        Position oldAgentPos = getAgentPosition();
        Position oldBoxPos   = getBoxPosition();
        Position newAgentPos, newBoxPos;
        HTNState result;

        // keep track of validity of the action
        boolean valid =  true;
        debug(this.toString() );

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

                result = new HTNState(newAgentPos, oldBoxPos);
                debug(" + " + moveAction.toString() + " -> " + result.toString() );

                if (result.isLegal()) {
                    debug("", -2);
                    return result;
                } else {
                    debug("HTNState.applyMove: Invalid result " + result.toString(), -2);
                    return null;
                }
            }
            case PUSH: {

                PushConcreteAction pushAction = (PushConcreteAction) concreteAction;

                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxDirection());
                result = new HTNState(newAgentPos, newBoxPos);

                debug(" + " + pushAction.toString() + " -> " + result.toString());
                // check preconditions !!! THIS IS PUSH

                valid &= !pushAction.getAgentDirection().getInverse().equals(pushAction.getBoxDirection()); // NOT opposite directions (would be pull!)
                debug(" validation push not opposite directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newAgentPos.equals(oldBoxPos);        // Push: agent follows box
                debug(" validation push agent follows box:" + Boolean.toString(valid));

                valid &= !newBoxPos.equals(oldAgentPos);       // Push: agent is not at wrong location
                debug(" validation push box is not at old agent location:" + Boolean.toString(valid));

                break;
            }
            case PULL: {

                PullConcreteAction pullAction = (PullConcreteAction) concreteAction;

                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxDirection().getInverse());
                result = new HTNState(newAgentPos, newBoxPos);

                debug(" + " + pullAction.toString() + " -> " + result.toString());

                // check preconditions !!! THIS IS PULL
                valid &= !pullAction.getAgentDirection().equals(pullAction.getBoxDirection()); // NOT same directions (would be push)
                debug(" validation pull not same directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newBoxPos.equals(oldAgentPos);        // Pull: box follows agent
                debug(" validation pull box follows agent :" + Boolean.toString(valid));

                valid &= (!newAgentPos.equals(oldBoxPos));       // Pull: agent is not at wrong location
                debug(" validation pull agent is not at old box location:" + Boolean.toString(valid));

                break;
            }
            case NONE: {
                debug("", -2);
                return this;
            }
            default:
                debug("", -2);
                throw new UnsupportedOperationException("Invalid action type");
        }
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        debug(" validation box and agent is neighbor in prior state:" + Boolean.toString(valid));

        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state
        debug(" validation box and agent are still neighbours in posterior state:" + Boolean.toString(valid));

        valid &= result.isLegal();
        debug(" validation the new state is legal:" + Boolean.toString(valid));

        debug("", -2);
        return (valid) ? result : null;
    }

    public boolean isLegal() { // we could introduce different levels of relaxations to be enforced here
        debug("isLegal():", 2);

        boolean valid = !agentPosition.equals(boxPosition);
        debug("box and agent position are not identical:" + Boolean.toString(valid) );

        valid &= !GlobalLevelService.getInstance().isWall(agentPosition);
        debug("agent is not at wall:" + Boolean.toString(valid) );

        if (boxPosition != null) {
            valid &= !GlobalLevelService.getInstance().isWall(boxPosition);
            debug("box is not at wall:" + Boolean.toString(valid) );
        }
        debug("", -2);
        return valid;
    }

    /**
     * What does this do? As far as i can see it does not return anything meaningful
     * Mads: Well it is meaningfull to check whether states have been visited before in the planning loop
     * and to check if 2 states are equal... this method comes in handy
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNState other = (HTNState) obj;
        if (!agentPosition.equals(other.getAgentPosition()))
            return false;
        if ((boxPosition == null) || (other.getBoxPosition() == null)) {
            return boxPosition == other.getBoxPosition();
        }
        if (!boxPosition.equals(other.getBoxPosition()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentPosition.hashCode();
        result = prime * result + boxPosition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String s;
        s  = "State:[Ag:" + agentPosition.toString();
        s += ",Bx:" + ((boxPosition!=null) ? boxPosition.toString() : "null") + "]";
        return s;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }
}
