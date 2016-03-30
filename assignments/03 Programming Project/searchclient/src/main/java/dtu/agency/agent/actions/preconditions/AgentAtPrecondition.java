package dtu.agency.agent.actions.preconditions;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;

public class AgentAtPrecondition extends Precondition {

    private Agent agent;
    private Position agentPreconditionPosition;

    public AgentAtPrecondition(Agent agent, Position agentPreconditionPosition) {
        this.agent = agent;
        this.agentPreconditionPosition = agentPreconditionPosition;
    }

    public Position getAgentPreconditionPosition() {
        return agentPreconditionPosition;
    }

    public Agent getAgent() {
        return agent;
    }
}
