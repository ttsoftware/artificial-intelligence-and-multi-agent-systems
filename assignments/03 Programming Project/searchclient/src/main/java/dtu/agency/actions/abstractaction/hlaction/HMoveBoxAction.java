package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;

import java.io.Serializable;


/**
 * This Action Moves a box and returns the agent to the box origin
 */
public class HMoveBoxAction extends HLAction implements Serializable {

    private final Box box;
    private final Position boxDestination;
    private final Position agentDestination;

    /**
     *
     * @param box The box to be moved
     * @param boxDestination The destination of the box
     * @param agentDestination The destination of the agent
     * @throws AssertionError
     */
    public HMoveBoxAction(Box box, Position boxDestination, Position agentDestination) throws AssertionError {
        this.box = box;
        this.boxDestination = boxDestination;
        this.agentDestination = agentDestination;
        if (box == null || boxDestination == null || agentDestination == null) {
            throw new AssertionError("HMoveBoxAction: null values not accepted for box or agentDestination");
        }
    }

    /**
     * copy constructor
     * @param other The other HMoveBoxAction to be deep copied
     */
    public HMoveBoxAction(HMoveBoxAction other) {
        this.box = new Box(other.getBox());
        this.boxDestination = new Position(other.getBoxDestination());
        this.agentDestination = new Position(other.getAgentDestination());
    }

    @Override
    public Position getBoxDestination() { return boxDestination; }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.HMoveBoxAndReturn;
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
        String s = "HMoveBoxAction(" +
                getBox().toString() +
                "->" +
                getBoxDestination().toString() +
                "," +
                getAgentDestination().toString() +
                ")";
        return s;
    }

    @Override
    public int approximateSteps(PlanningLevelService pls) {
        Position boxPosition = pls.getPosition(box);
        debug("agent " + agent);
        Position agentPosition = pls.getPosition(agent);

        int approximation = 0;
        approximation += agentPosition.manhattanDist(boxPosition) -1;
        approximation += boxPosition.manhattanDist(boxDestination);
        approximation += boxDestination.manhattanDist(agentDestination);

        return approximation;
    }

}
