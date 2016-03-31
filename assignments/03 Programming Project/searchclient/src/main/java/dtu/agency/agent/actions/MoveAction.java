package dtu.agency.agent.actions;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;

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
