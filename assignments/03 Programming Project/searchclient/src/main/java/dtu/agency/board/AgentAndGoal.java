package dtu.agency.board;

public class AgentAndGoal extends BoardObject {

    private final Agent agent;
    private final Goal goal;

    public AgentAndGoal(Agent agent, Goal goal) {
        super("(" + agent.getLabel() + goal.getLabel() + ")");
        this.agent = agent;
        this.goal = goal;
    }

    public AgentAndGoal(AgentAndGoal other) {
        super(other.getLabel());
        this.agent = new Agent (other.getAgent());
        this.goal = new Goal (other.getGoal());
    }

    public Agent getAgent() {
        return agent;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public BoardCell getType() {
        return BoardCell.AGENT_GOAL;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AgentAndGoal) {
            return super.equals(object);
        }
        return false;
    }

}
