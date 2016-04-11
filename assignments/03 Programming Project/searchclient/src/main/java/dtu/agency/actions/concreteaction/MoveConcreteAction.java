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
        super();
        this.agent = new Agent(other.getAgent());
        this.direction = other.getDirection();
        this.agentPosition = new Position(other.getAgentPosition());
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
