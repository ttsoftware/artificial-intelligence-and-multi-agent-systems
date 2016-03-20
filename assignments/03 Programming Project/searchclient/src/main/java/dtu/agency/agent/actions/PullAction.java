package dtu.agency.agent.actions;

public class PullAction extends Action {

    private Direction agentDirection;
    private Direction boxDirection;

    public PullAction(Direction agentDirection, Direction boxDirection) {
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public ActionType getType() {
        return ActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }
}
