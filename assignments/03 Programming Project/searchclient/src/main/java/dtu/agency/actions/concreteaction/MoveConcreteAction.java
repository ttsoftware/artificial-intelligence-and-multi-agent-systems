package dtu.agency.actions.concreteaction;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;

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

    public MoveConcreteAction(MoveConcreteAction other) {
        this.agent = (other.getAgent()!=null) ? new Agent(other.getAgent()) : null;
        this.direction = other.getDirection();
        this.agentPosition = (other.getAgentPosition()!=null) ? new Position(other.getAgentPosition()) : null;
        this.heuristicValue = other.getHeuristicValue();
    }

    @Override
    public ConcreteActionType getType() {
        return ConcreteActionType.MOVE;
    }

    @Override
    public String toString() {
        return "Move(" + getDirection() + ")";
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Direction getAgentDirection() {
        return direction;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Agent getAgent() {
        return agent;
    }
}
