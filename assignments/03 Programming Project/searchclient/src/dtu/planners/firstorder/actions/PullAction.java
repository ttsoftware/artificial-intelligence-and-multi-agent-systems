package dtu.planners.firstorder.actions;

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
}
