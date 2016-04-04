package dtu.agency.agent.actions;

import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;

public class MoveAction extends Action {

    private Direction direction;

    public MoveAction(Direction agentDirection) {
        this.direction = agentDirection;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }

    @Override
    public HTNState applyTo(HTNState oldState) {
        Position oldAgentPos = oldState.getAgentPosition();
        Position newAgentPos = new Position(oldAgentPos, direction);
        Position boxPos = oldState.getBoxPosition();

        HTNState result = new HTNState(newAgentPos, boxPos);
        boolean valid = result.isLegal();

        return (valid) ? result : null;
    }

    @Override
    public String toString() {
        return "Move(" + getDirection() + ")";
    }
}
