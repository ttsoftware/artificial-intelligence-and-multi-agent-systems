package dtu.agency.planners.firstorder.actions;

public class PullAction extends Action {

    public PullAction(Direction agentDirection, Direction boxDirection) {

    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }
}
