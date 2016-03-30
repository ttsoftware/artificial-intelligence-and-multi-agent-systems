package dtu.agency.agent.actions;

import dtu.agency.agent.actions.effects.Effect;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Position;

import java.io.Serializable;
import java.util.List;

public abstract class Action implements Serializable {

    public abstract ActionType getType();

    public abstract List<Precondition> getPreconditions();

    public abstract List<Effect> getEffects();

    @Override
    public abstract String toString();

    public abstract int getHeuristic();

    public abstract void setHeuristic(int heuristic);

    public Position getNextPositionFromMovingDirection(Position position, Direction direction) {
        switch (direction) {
            case NORTH:
                return new Position(position.getRow()-1, position.getColumn());
            case SOUTH:
                return new Position(position.getRow()+1, position.getColumn());
            case WEST:
                return new Position(position.getRow(), position.getColumn()-1);
            case EAST:
                return new Position(position.getRow(), position.getColumn()+1);
            default:
                return position;
        }
    }
}