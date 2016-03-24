package dtu.agency.planners.actions.preconditions;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;

public class AgentAtPrecondition extends Precondition {

    private Agent agent;
    private Position position;

    public AgentAtPrecondition(Agent agent, Position position) {
        this.agent = agent;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Agent getAgent() {
        return agent;
    }
}
