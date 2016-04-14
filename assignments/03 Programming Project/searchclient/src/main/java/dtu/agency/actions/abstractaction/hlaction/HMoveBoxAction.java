package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;

import java.io.Serializable;


// TODO: THIS HLAction cannot be solved by HTNPlanner, must be revised!!!
/**
* This Action Moves a box and returns the agent to the box origin
*/
public class HMoveBoxAction extends HLAction implements Serializable {

    private final Box box;
    private final Position boxDestination;
    private final Position agentDestination;

    public HMoveBoxAction(Box box, Position boxDestination, Position agentDestination) throws AssertionError {
        this.box = box;
        this.boxDestination = boxDestination;
        this.agentDestination = agentDestination;
        if (box == null || boxDestination == null || agentDestination == null) {
            throw new AssertionError("HMoveBoxAction: null values not accepted for box or agentDestination");
        }
    }

    public HMoveBoxAction(HMoveBoxAction other) {
        this.box = new Box(other.getBox());
        this.boxDestination = new Position(other.getAgentDestination());
        this.agentDestination = new Position(other.getAgentDestination());
    }

    @Override
    public Position getBoxDestination() { return boxDestination; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.MoveBoxAndReturn;
    }

    @Override
    public Position getAgentDestination() {
        return agentDestination;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("HMoveBoxAction(");
        s.append(getBox().toString());
        s.append("->");
        s.append(getBoxDestination().toString());
        s.append(",");
        s.append(getAgentDestination().toString());
        s.append(")");
        return s.toString();
    }

    @Override
    public int approximateSteps(Position agentOrigin) {
        // TODO: Planning level service instead??
        Position boxCurrentPosition = BDIService.getInstance().getBDILevelService().getPosition(box);

        int approximation = 0;
        approximation += agentOrigin.manhattanDist(boxCurrentPosition) -1;
        approximation += boxCurrentPosition.manhattanDist(boxDestination);
        approximation += boxDestination.manhattanDist(agentDestination);

        return approximation;
    }

}
