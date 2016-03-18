package dtu.planners.firstorder.actions;

public class PushAction extends Action {

    public PushAction(Direction agentDirection, Direction boxDirection) {

    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }
}
