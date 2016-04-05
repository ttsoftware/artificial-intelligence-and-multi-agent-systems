package dtu.agency.actions.concrete;

import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;

public class PushAction extends Action {

    private final Box box;
    private final Direction agentDirection;  // direction to box from agent POV
    private final Direction boxDirection;    // desired direction the box should move

    public PushAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    public Box getBox() {
        return box;
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUSH;
    }

    @Override
    public HTNState applyTo(HTNState oldState) {
        // calculate new effects
        Position oldAgentPos = oldState.getAgentPosition();
        Position oldBoxPos = oldState.getBoxPosition();

        Position newAgentPos = new Position(oldAgentPos, agentDirection);
        Position newBoxPos = new Position(oldBoxPos, boxDirection);

        boolean valid = true;
        // check preconditions !!! THIS IS PUSH
        valid &= !agentDirection.getInverse().equals(boxDirection); // NOT opposite directions (would be pull!)
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        // postconditions
        valid &= newAgentPos.equals(oldBoxPos);        // agent is at correct location
        valid &= !newBoxPos.equals(oldAgentPos);       // box and agent has not switched positions
        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state
        HTNState result = new HTNState(newAgentPos, newBoxPos);
        valid &= result.isLegal();
        return (valid) ? result : null;
    }

    @Override
    public String toString() {
        return "Push(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }
}
