package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;

import dtu.agency.planners.htn.HTNState;
import dtu.agency.services.LevelService;

public class MoveConcreteAction extends ConcreteAction {

    private Direction direction;
    private Agent agent = null;
    private Position agentPosition = null;

    public MoveConcreteAction(Direction agentDirection) {
        this.direction = agentDirection;
    }

    public MoveConcreteAction(Agent agent, Position agentPosition, Direction agentDirection, int heuristicValue) {
        this.direction = agentDirection;
        this.agent = agent;
        this.agentPosition = agentPosition;
        this.heuristicValue = heuristicValue;
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.MOVE;
    }

    @Override
    public HTNState applyTo(HTNState oldState) {
        Position oldAgentPos = oldState.getAgentPosition();
        Position newAgentPos = LevelService.getInstance().getPositionInDirection(oldAgentPos, direction);
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
