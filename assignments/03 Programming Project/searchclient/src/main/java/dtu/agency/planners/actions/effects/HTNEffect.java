package dtu.agency.planners.actions.effects;

import dtu.agency.agent.actions.Direction;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

/**
 * Created by Mads on 3/22/16.
 */
public class HTNEffect extends Effect {
    private final Position agentPosition;
    private final Position boxPosition;

    public HTNEffect(Position agent, Position targetbox){
        this.agentPosition = agent;
        this.boxPosition = targetbox;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public boolean equals(HTNEffect o){
        if ( getAgentPosition().equals( o.getAgentPosition() ) )
            if ( getBoxPosition().equals( o.getBoxPosition() ) ) {
                return true;
            }
        return false;
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
    public boolean isLegal() {
        boolean valid = true;
        valid &= LevelService.getInstance().getLevel().notWall(this.getAgentPosition());
        valid &= LevelService.getInstance().getLevel().notWall(this.getBoxPosition());
        return valid;
    }


    public Direction getDirectionToBox() { // returns the direction from agent to box
        return agentPosition.getDirectionTo(boxPosition);
    }


    public boolean boxIsMovable() {
        return agentPosition.isNeighbour(boxPosition);
    }
}
