package dtu.agency.actions.abstractaction;

import dtu.agency.actions.AbstractAction;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;

public class MoveBoxAbstractAction extends AbstractAction {

    private Box box;
    private Goal goal;

    public MoveBoxAbstractAction(Box box, Goal goal) {
        this.box = box;
        this.goal = goal;
    }

    public Box getBox() {
        return box;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.MoveBoxAction;
    }
}
