package dtu.agency.planners.htn;

import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

public class HTNState {

    private final Position agentPosition;
    private final Position boxPosition;

    public HTNState(Position agentPosition, Position boxPosition) {
        this.agentPosition = agentPosition;
        this.boxPosition = boxPosition;
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return LevelService.getInstance().getRelativeDirection(agentPosition, boxPosition, false);
    }

    public boolean boxIsMovable() {
        return agentPosition.isAdjacentTo(boxPosition);
    }

    public boolean isLegal() { // we could introduce different levels of relaxations to be enforced here
        boolean valid = true;
        //System.err.println(!getAgentPosition().equals(getBoxPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getAgentPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getBoxPosition()));
        valid &= !agentPosition.equals(boxPosition);
        valid &= !LevelService.getInstance().isWall(agentPosition);
        if (boxPosition != null) {
            valid &= !LevelService.getInstance().isWall(boxPosition);
        }
        return valid;
    }

    /**
     * What does this do? As far as i can see it does not return anything meaningful
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNState other = (HTNState) obj;
        if (!agentPosition.equals(other.getAgentPosition()))
            return false;
        if ((boxPosition == null) || (other.getBoxPosition() == null)) {
            return boxPosition == other.getBoxPosition();
        }
        if (!boxPosition.equals(other.getBoxPosition()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentPosition.hashCode();
        result = prime * result + boxPosition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("State:[Ag:" + agentPosition.toString());
        s.append(",Bx:" + boxPosition.toString() + "]");
        return s.toString();
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }
}
