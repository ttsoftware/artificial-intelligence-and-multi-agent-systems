package dtu.agency.board;

public class AgentAndGoal extends BoardObject {

    private final Agent agent;
    private final Goal goal;

    public AgentAndGoal(Agent agent, Goal goal) {
        super(agent.getLabel() + goal.getLabel());
        this.agent = agent;
        this.goal = goal;
    }

    public Agent getAgent() {
        return agent;
    }

    public Goal getGoal() {
        return goal;
    }
}
