package dtu.agent.actions;

public class MoveAction extends Action {

    private Direction direction;

    public MoveAction(Direction agentDirection) {
        this.direction = direction;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }
}
