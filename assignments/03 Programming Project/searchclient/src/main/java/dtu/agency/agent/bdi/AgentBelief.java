package dtu.agency.agent.bdi;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.GlobalLevelService;

import java.util.HashMap;
import java.util.List;


public class AgentBelief {
    // should reflect where the agentCurrentPosition thinks the box(es) currently targeted are positioned
    Position agentCurrentPosition;                   // agent's believed own position
    HashMap<String, Position> currentBoxPositions;   // agent's believed currentBoxPositions position

    public AgentBelief(Agent agent ) {
        agentCurrentPosition = GlobalLevelService.getInstance().getPosition(agent);
        List<Box> boxList = GlobalLevelService.getInstance().getLevel().getBoxes();
        this.currentBoxPositions = new HashMap<>();
        for (Box b : boxList) {
            currentBoxPositions.put(b.getLabel(), GlobalLevelService.getInstance().getPosition(b));
        }
    }

    public Position getAgentCurrentPosition() {
        return agentCurrentPosition;
    }

    public void updateMyPosition(ConcreteAction action) {
        switch (action.getType()) {
            case NONE:
                break;
            default:
                //agentCurrentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(agentCurrentPosition, action.getAgentDirection());
                break;
        }
    }

    public Position getBoxPosition(String boxLabel) {
        return currentBoxPositions.get(boxLabel);
    }

    public void setBoxPosition(String boxLabel, Position boxPosition) {
        currentBoxPositions.put(boxLabel, boxPosition);
    }

    public void updateBoxPosition(String targetBox, ConcreteAction action) {
        Position oldBoxPosition = currentBoxPositions.get(targetBox);
        Position newBoxPosition;
        switch (action.getType()) {
            case MOVE:
                break;
            case PUSH:
                PushConcreteAction push = (PushConcreteAction) action;
                newBoxPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                        oldBoxPosition,
                        push.getBoxDirection()
                );
                currentBoxPositions.put(targetBox, newBoxPosition);
                break;
            case PULL:
                PullConcreteAction pull = (PullConcreteAction) action;
                newBoxPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                        oldBoxPosition,
                        pull.getBoxDirection()
                );
                currentBoxPositions.put(targetBox, newBoxPosition);
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
}
