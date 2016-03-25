package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;

public class AgentAtPrecondition extends Precondition {

    private Agent agent;
    private Position agentPosition;

    public AgentAtPrecondition(Agent agent, Position agentPosition) {
        this.agent = agent;
        this.agentPosition = agentPosition;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Agent getAgent() {
        return agent;
    }
}
