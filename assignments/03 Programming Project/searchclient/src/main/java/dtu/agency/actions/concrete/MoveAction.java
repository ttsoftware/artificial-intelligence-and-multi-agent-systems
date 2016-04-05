package dtu.agency.actions.concrete;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;

import dtu.agency.planners.htn.HTNState;

public class MoveAction extends Action {

    private Direction direction;
    private Agent agent = null;
    private Position agentPosition = null;

    public MoveAction(Direction agentDirection) {
        this.direction = agentDirection;
    }

    public MoveAction(Agent agent, Position agentPosition, Direction agentDirection, int heuristicValue) {
        this.direction = agentDirection;
        this.agent = agent;
        this.agentPosition = agentPosition;
        this.heuristicValue = heuristicValue;
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

    public Direction getDirection() {
        return direction;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Agent getAgent() {
        return agent;
    }
}
