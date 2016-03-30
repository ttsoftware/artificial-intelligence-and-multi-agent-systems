package dtu.agency.agent.actions;

import dtu.agency.agent.actions.effects.Effect;
import dtu.agency.agent.actions.preconditions.AgentAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;

import java.util.ArrayList;
import java.util.List;

public class MoveAction extends Action {

    private Direction direction;
    private Agent agent = null;
    private Position agentPosition = null;
    private int heuristic;

    public MoveAction(Direction agentDirection) {
        this.direction = agentDirection;
    }

    public MoveAction(Direction agentDirection, Agent agent, Position agentPosition) {
        this.direction = agentDirection;
        this.agent = agent;
        this.agentPosition = agentPosition;
    }

    @Override
    public List<Precondition> findPreconditions() {
        List<Precondition> preconditions = new ArrayList<>();
        preconditions.add(new AgentAtPrecondition(agent, agentPosition));

        return preconditions;
    }

    @Override
    public int getHeuristic() {
        return heuristic;
    }

    @Override
    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public List<Effect> findEffects() {
        return null;
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
