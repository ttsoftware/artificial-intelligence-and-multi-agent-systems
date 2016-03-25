package dtu.agency.agent.actions;

import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.HTNEffect;

public class MoveAction extends Action {

    private Direction direction;

    public MoveAction(Direction agentDirection) {
        this.direction = direction;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }

    @Override
    public String toString() {
        return "Move(" + getDirection() + ")";
    }

    @Override
    public HTNEffect applyTo(HTNEffect oldState) {
        Position oldAgentPos = oldState.getAgentPosition();
        Position newAgentPos = new Position(oldAgentPos, direction);
        Position boxPos = oldState.getBoxPosition();
        return new HTNEffect(newAgentPos, boxPos);
    }

    public Direction getDirection() {
        return direction;
    }


}
