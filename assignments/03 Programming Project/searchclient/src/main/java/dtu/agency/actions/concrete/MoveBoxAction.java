package dtu.agency.actions.concrete;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public abstract class MoveBoxAction extends Action {

    protected Box box;
    protected Agent agent;
    protected Position boxPosition;
    protected Position agentPosition;
    protected Direction boxDirection;
    protected Direction agentDirection;

    public MoveBoxAction(Box box, Agent agent, Position boxPosition, Position agentPosition, Direction boxDirection, Direction agentDirection) {
        this.box = box;
        this.agent = agent;
        this.boxPosition = boxPosition;
        this.agentPosition = agentPosition;
        this.boxDirection = boxDirection;
        this.agentDirection = agentDirection;
    }

    public Box getBox() {
        return box;
    }

    public Agent getAgent() {
        return agent;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }
}
