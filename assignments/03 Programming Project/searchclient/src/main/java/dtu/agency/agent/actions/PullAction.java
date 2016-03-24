package dtu.agency.agent.actions;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.planners.actions.preconditions.AgentAtPrecondition;
import dtu.agency.planners.actions.preconditions.FreeCellPrecondition;
import dtu.agency.planners.actions.preconditions.NeighbourPrecondition;
import dtu.agency.planners.actions.preconditions.Precondition;

import java.util.ArrayList;
import java.util.List;

public class PullAction extends Action {

    private final Box box;
    private Position boxPosition = null;
    private Agent agent = null;
    private Position agentPosition = null;
    private final Direction agentDirection;
    private final Direction boxDirection;

    public PullAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    public PullAction(Box box, Position boxPosition, Agent agent, Position agentPosition, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.boxPosition = boxPosition;
        this.agent = agent;
        this.agentPosition = agentPosition;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public List<Precondition> getPreconditions() {
        List<Precondition> preconditions = new ArrayList<>();
        Position nextPosition = ActionHelper.getNextPositionFromMovingDirection(getAgentPosition(), getAgentDirection());
        preconditions.add(new FreeCellPrecondition(nextPosition));
        preconditions.add(new AgentAtPrecondition(getAgent(), getAgentPosition()));
        preconditions.add(new AgentAtPrecondition(getBox(), getBoxPosition()));
        preconditions.add(new NeighbourPrecondition(getAgent(), nextPosition));

        return preconditions;
    }

    @Override
    public List<Effect> getEffects() {
        return null;
    }

    @Override
    public ActionType getType() {
        return ActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }

    public Box getBox() {
        return box;
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public Agent getAgent() {
        return agent;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }
}
