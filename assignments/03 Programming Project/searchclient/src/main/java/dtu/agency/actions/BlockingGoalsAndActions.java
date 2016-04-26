package dtu.agency.actions;

import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Goal;

import java.util.List;
import java.util.Stack;

public class BlockingGoalsAndActions {

    private List<Goal> blockingGoals;

    private Stack<MoveConcreteAction> actions;

    public BlockingGoalsAndActions(Stack<MoveConcreteAction> actions, List<Goal> blockingGoals) {
        this.actions = actions;
        this.blockingGoals = blockingGoals;
    }

    public List<Goal> getBlockingGoals() {
        return blockingGoals;
    }

    public void setBlockingGoals(List<Goal> blockingGoals) {
        this.blockingGoals = blockingGoals;
    }

    public Stack<MoveConcreteAction> getActions() {
        return actions;
    }

    public void setActions(Stack<MoveConcreteAction> actions) {
        this.actions = actions;
    }
}
