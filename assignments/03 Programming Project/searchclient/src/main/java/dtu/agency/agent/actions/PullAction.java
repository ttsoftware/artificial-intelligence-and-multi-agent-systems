package dtu.agency.agent.actions;

import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.HTNEffect;

public class PullAction extends Action {

    private final Box box;
    private final Direction agentDirection;
    private final Direction boxDirection;

    public PullAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public ActionType getType() {
        return ActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }

    @Override
    public HTNEffect applyTo(HTNEffect oldState) {
        // calculate new effects
        Position oldAgentPos = oldState.getAgentPosition();
        Position oldBoxPos = oldState.getBoxPosition();

        Position newAgentPos = new Position(oldAgentPos, agentDirection);
        Position newBoxPos = new Position(oldBoxPos, boxDirection.getInverse());

        boolean valid = true;
        // check preconditions !!! THIS IS PULL
        valid &= !agentDirection.equals(boxDirection); // NOT same directions (would be push)
        valid &= oldAgentPos.isNeighbour(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        // post conditions
        valid &= newBoxPos.equals(oldAgentPos);        // box and agent has not switched positions
        valid &= !newAgentPos.equals(oldBoxPos);       // agent is not at wrong location
        valid &= newAgentPos.isNeighbour(newBoxPos);   // box and agent are still neighbours in posterior state
        // should the actual box be a part of the effect??
        // we do not have access to level, thus we cannot check box label.

        return (valid) ? new HTNEffect(newAgentPos, newBoxPos) : null;
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
}
