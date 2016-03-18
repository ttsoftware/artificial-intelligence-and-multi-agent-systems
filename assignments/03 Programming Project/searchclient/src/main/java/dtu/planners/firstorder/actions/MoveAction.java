package dtu.planners.firstorder.actions;

public class MoveAction extends Action {

    public MoveAction(Direction agentDirection) {

    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }
}
