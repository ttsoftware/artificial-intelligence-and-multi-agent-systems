package dtu.agency.planners.actions;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;

public class MoveBoxAction extends AbstractAction {

    private Box box;
    private Goal goal;

    public MoveBoxAction(Box box, Goal goal) {
        this.box = box;
        this.goal = goal;
    }

    public Box getBox() {
        return box;
    }

    public Goal getGoal() {
        return goal;
    }
}
