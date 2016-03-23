package dtu.agency.planners.actions;

import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.planners.actions.preconditions.Precondition;

import java.util.Collection;

public abstract class AbstractAction {

    private Collection<Precondition> preconditions;
    private Collection<Effect> effects;

    private Position position;

    public Collection<Effect> getEffects() {
        return effects;
    }

    public void setEffects(Collection<Effect> effects) {
        this.effects = effects;
    }

    public Collection<Precondition> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(Collection<Precondition> preconditions) {
        this.preconditions = preconditions;
    }

    public Position getPosition() {
        return position;
    }
}
