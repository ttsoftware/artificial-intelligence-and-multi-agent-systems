package dtu.agency.agent.actions;

public class PushAction extends Action {

    private Direction agentDirection;
    private Direction boxDirection;

    public PushAction(Direction agentDirection, Direction boxDirection) {
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUSH;
    }
}
