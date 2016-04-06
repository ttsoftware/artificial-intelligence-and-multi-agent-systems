package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.services.LevelService;

public class PullConcreteAction extends ConcreteAction {

    private final Box box;
    private final Direction agentDirection;
    private final Direction boxDirection;

    public PullConcreteAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + agentDirection + "," + boxDirection + ")";
    }

    @Override
    public HTNState applyTo(HTNState oldState) {
        Position oldAgentPos = oldState.getAgentPosition();
        Position oldBoxPos = oldState.getBoxPosition();

        Position newAgentPos = LevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, agentDirection);
        Position newBoxPos = LevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, boxDirection.getInverse());

        boolean valid = true;
        // check preconditions !!! THIS IS PULL
        valid &= !agentDirection.equals(boxDirection); // NOT same directions (would be push)
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        // post conditions
        valid &= newBoxPos.equals(oldAgentPos);        // box and agent has not switched positions
        valid &= !newAgentPos.equals(oldBoxPos);       // agent is not at wrong location
        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state

        HTNState result = new HTNState(newAgentPos, newBoxPos);
        valid &= result.isLegal();

        return (valid) ? result : null;
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