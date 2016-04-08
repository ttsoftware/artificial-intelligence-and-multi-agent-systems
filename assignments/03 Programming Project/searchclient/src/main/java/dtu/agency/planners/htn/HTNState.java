package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

public class HTNState {

    private final Position agentPosition;
    private final Position boxPosition;

    public HTNState(Position agentPosition, Position boxPosition) {
        this.agentPosition = agentPosition;
        this.boxPosition = boxPosition;
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return LevelService.getInstance().getRelativeDirection(agentPosition, boxPosition, false);
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

        // calculate new effects
        Position oldAgentPos = getAgentPosition();
        Position oldBoxPos = getBoxPosition();

        Position newAgentPos;
        Position newBoxPos;

        // precondition(s)
        boolean valid =  true;

        //System.err.print(this.toString() );

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

                HTNState result = new HTNState(newAgentPos, oldBoxPos);

                //System.err.println(result.toString());
                if (result.isLegal()) {
                    return result;
                } else {
                    //System.err.println("HTNState.applyMove: Invalid result " + result.toString());
                    return null;
                }
            }
            case PUSH: {

                PushConcreteAction pushAction = (PushConcreteAction) concreteAction;

                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = LevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxDirection());

                //System.err.print(" + " + pushAction.toString() + " -> " + newAgentPos.toString() + " " + newBoxPos.toString());
                // check preconditions !!! THIS IS PUSH

                valid &= !pushAction.getAgentDirection().getInverse().equals(pushAction.getBoxDirection()); // NOT opposite directions (would be pull!)
                //System.err.println(" validation push not opposite directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newAgentPos.equals(oldBoxPos);        // Push: agent follows box
                //System.err.println(" validation push agent follows box:" + Boolean.toString(valid));

                valid &= !newBoxPos.equals(oldAgentPos);       // Push: agent is not at wrong location
                //System.err.println(" validation push box is not at old agent location:" + Boolean.toString(valid));

                break;
            }
            case PULL: {

                PullConcreteAction pullAction = (PullConcreteAction) concreteAction;

                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = LevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxDirection().getInverse());

                //System.err.print(" + " + pullAction.toString() + " -> " + newAgentPos.toString() + " " + newBoxPos.toString());
                // check preconditions !!! THIS IS PULL
                valid &= !pullAction.getAgentDirection().equals(pullAction.getBoxDirection()); // NOT same directions (would be push)
                //System.err.println(" validation pull not same directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newBoxPos.equals(oldAgentPos);        // Pull: box follows agent
                //System.err.println(" validation pull box follows agent :" + Boolean.toString(valid));

                valid &= (!newAgentPos.equals(oldBoxPos));       // Pull: agent is not at wrong location
                //System.err.println(" validation pull agent is not at old box location:" + Boolean.toString(valid));

                break;
            }
            case NONE: {
                return this;
            }
            default:
                throw new UnsupportedOperationException("Invalid action type");
        }
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        //System.err.println(" validation box and agent is neighbor in prior state:" + Boolean.toString(valid));

        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state
        //System.err.println(" validation box and agent are still neighbours in posterior state:" + Boolean.toString(valid));

        HTNState result = new HTNState(newAgentPos, newBoxPos);
        valid &= result.isLegal();
        //System.err.println(" validation: is the new state even legal?:" + Boolean.toString(valid));

        return (valid) ? result : null;
    }

    public boolean isLegal() { // we could introduce different levels of relaxations to be enforced here
        boolean valid = true;
        //System.err.println(!getAgentPosition().equals(getBoxPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getAgentPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getBoxPosition()));
        valid &= !agentPosition.equals(boxPosition);
        valid &= !LevelService.getInstance().isWall(agentPosition);
        if (boxPosition != null) {
            valid &= !LevelService.getInstance().isWall(boxPosition);
        }
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
        StringBuilder s = new StringBuilder();
        s.append("State:[Ag:" + agentPosition.toString());
        s.append(",Bx:" + ((boxPosition!=null) ? boxPosition.toString() : "null") + "]");
        return s.toString();
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }
}
