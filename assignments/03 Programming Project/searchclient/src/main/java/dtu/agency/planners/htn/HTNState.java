package dtu.agency.planners.htn;

import dtu.agency.agent.actions.Direction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.services.LevelService;

/**
 * Created by Mads on 3/22/16.
 */
public class HTNState {
    private final Position agentPosition;
    private final Position boxPosition;

    public HTNState(Position agent, Position targetBox){
        this.agentPosition = agent;
        this.boxPosition = targetBox;
    }

    public HTNState(Agent agent, Box targetBox){
        this.agentPosition = agent.getPosition();
        this.boxPosition = targetBox.getPosition();
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return agentPosition.getDirectionTo(boxPosition);
    }

    public boolean boxIsMovable() {
        return agentPosition.isNeighbour(boxPosition);
    }

    public boolean isLegal() { // we could introduce different levels of relaxations to be enforced here
        boolean valid = true;
        //System.err.println(!getAgentPosition().equals(getBoxPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getAgentPosition()));
        //System.err.println(LevelService.getInstance().getLevel().notWall(this.getBoxPosition()));
        valid &= !getAgentPosition().equals(getBoxPosition());
        valid &= LevelService.getInstance().getLevel().notWall(this.getAgentPosition());
        if (boxPosition != null) {
            valid &= LevelService.getInstance().getLevel().notWall(this.getBoxPosition());
        }
        return valid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNState other = (HTNState) obj;
        if (!agentPosition.equals( other.getAgentPosition()) )
            return false;
        if ((boxPosition == null) || (other.getBoxPosition() == null)) {
            return boxPosition == other.getBoxPosition();
        }
        if (!boxPosition.equals(other.getBoxPosition()) )
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
        s.append("Eff:[A:");
        if (getAgentPosition()!= null) {
            s.append(getAgentPosition().toString());
        } else {
            s.append("null");
        }
        s.append(",B:");
        if (getBoxPosition()!= null) {
            s.append(getBoxPosition().toString());
        } else {
            s.append("null");
        }
        s.append("]");
        return s.toString();
    }

}
