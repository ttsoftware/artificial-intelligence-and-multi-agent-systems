package dtu.agency.agent.actions;

import dtu.agency.board.Box;

public class PullAction extends Action {

    private final Box box;
    private final Direction agentDirection;
    private final Direction boxDirection;

    public PullAction(Box box, Direction agentDirection, Direction boxDirection) {
        this.box = box;
        this.agentDirection = agentDirection;
        this.boxDirection = boxDirection;
    }

    @Override
    public ActionType getType() {
        return ActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + getAgentDirection() + "," + getBoxDirection() + ")";
    }

    public Box getBox() {
        return box;
    }

    public Direction getAgentDirection() {
        return agentDirection;
    }

    public Direction getBoxDirection() {
        return boxDirection;
    }
}
