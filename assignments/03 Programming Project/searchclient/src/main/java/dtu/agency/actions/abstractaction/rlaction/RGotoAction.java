package dtu.agency.actions.abstractaction.rlaction;

import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

public class RGotoAction extends RLAction {

    private final Position agentDestination;
    private final Box boxAtDestination;

    public RGotoAction(Position agentDestination) {
        this.boxAtDestination = null;
        this.agentDestination = agentDestination;
        if (agentDestination == null) throw new AssertionError("Constructing RGotoAction without destination");
    }

    public RGotoAction(Box box, Position agentDestination) {
        this.boxAtDestination = box;
        this.agentDestination = agentDestination;
        if (agentDestination == null) throw new AssertionError("Constructing RGotoAction without destination");
        if (box == null) throw new AssertionError("Constructing RGotoAction with null box");}

    public RGotoAction(RGotoAction other) {
        if(other.getBox() != null) {
            this.boxAtDestination = new Box(other.getBox());
        }
        else {
            boxAtDestination = null;
        }
        agentDestination = new Position(other.getAgentDestination());
    }

    @Override
    public AbstractActionType getType() {
        return AbstractActionType.RGotoAction;
    }

    @Override
    public Position getAgentDestination() {
        return agentDestination;
    }

    @Override
    public Position getBoxDestination() {
        return null;
    }

    @Override
    public Box getBox() {
        return boxAtDestination;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("RGotoAction(");
        if (getBoxDestination() != null) {
            s.append(getBoxDestination().toString());
        } else {
            s.append(getAgentDestination().toString());
        }
        s.append(")");
        return s.toString();
    }

    @Override
    public int approximateSteps(PlanningLevelService pls) {
        return pls.getPosition(agent).manhattanDist(agentDestination);
    }

 }
