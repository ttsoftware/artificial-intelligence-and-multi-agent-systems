package dtu.agency.agent.actions;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.planners.actions.preconditions.AgentAtPrecondition;
import dtu.agency.planners.actions.preconditions.FreeCellPrecondition;
import dtu.agency.planners.actions.preconditions.NeighbourPrecondition;
import dtu.agency.planners.actions.preconditions.Precondition;

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
    public List<Precondition> getPreconditions() {
        List<Precondition> preconditions = new ArrayList<>();
        Position nextPosition = ActionHelper.getNextPositionFromMovingDirection(getAgentPosition(), getDirection());
        preconditions.add(new FreeCellPrecondition(nextPosition));
        preconditions.add(new AgentAtPrecondition(getAgent(), getAgentPosition()));
//        preconditions.add(new NeighbourPrecondition(getAgent(), nextPosition));

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
    public List<Effect> getEffects() {
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
