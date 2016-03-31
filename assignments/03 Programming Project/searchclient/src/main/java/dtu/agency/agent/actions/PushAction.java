package dtu.agency.agent.actions;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class PushAction extends MoveBoxAction {

    public PushAction(Box box,
                      Agent agent,
                      Position boxPosition,
                      Position agentPosition,
                      Direction boxDirection,
                      Direction agentDirection,
                      int heuristicValue) {
        super(box, agent, boxPosition, agentPosition, boxDirection, agentDirection);
        this.heuristicValue = heuristicValue;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUSH;
    }

    @Override
    public String toString() {
        return "Push(" + agentDirection + "," + boxDirection + ")";
    }
}
