package dtu.agency.agent.actions;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class PullAction extends MoveBoxAction {

    public PullAction(Box box,
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
        return ActionType.PULL;
    }

    @Override
    public String toString() {
        return "Pull(" + agentDirection + "," + boxDirection + ")";
    }
}
