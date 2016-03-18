package dtu.agency.agent.actions;

public class NoAction extends Action {

    public NoAction() {
        // I do nothing at all
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }
}
