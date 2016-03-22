package dtu.agency.planners.actions.effects;

import dtu.agency.board.Position;

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


}
