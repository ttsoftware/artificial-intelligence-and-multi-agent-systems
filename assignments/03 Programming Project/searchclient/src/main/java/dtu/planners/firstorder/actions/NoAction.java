package dtu.planners.firstorder.actions;

public class NoAction extends Action {

    public NoAction() {
        // I do nothing at all
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }
}
