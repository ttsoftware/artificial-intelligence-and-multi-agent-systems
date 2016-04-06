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

        boolean valid = true;

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

                HTNState result = new HTNState(newAgentPos, oldBoxPos);

                return (result.isLegal()) ? result : null;
            }
            case PUSH: {

                PushConcreteAction pushAction = (PushConcreteAction) concreteAction;

                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = LevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxDirection());

                // check preconditions !!! THIS IS PUSH
                valid &= !pushAction.getAgentDirection().getInverse().equals(pushAction.getBoxDirection()); // NOT opposite directions (would be pull!)
                break;
            }
            case PULL: {

                PullConcreteAction pullAction = (PullConcreteAction) concreteAction;

                newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = LevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxDirection().getInverse());

                // check preconditions !!! THIS IS PULL
                valid &= !pullAction.getAgentDirection().equals(pullAction.getBoxDirection()); // NOT same directions (would be push)
                break;
            }
            case NONE: {
                return this;
            }
            default:
                throw new UnsupportedOperationException("Invalid action type");
        }

        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        // post conditions
        valid &= newBoxPos.equals(oldAgentPos);        // box and agent has not switched positions
        valid &= !newAgentPos.equals(oldBoxPos);       // agent is not at wrong location
        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state

        HTNState result = new HTNState(newAgentPos, newBoxPos);
        valid &= result.isLegal();

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
        s.append(",Bx:" + boxPosition.toString() + "]");
        return s.toString();
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }
}
