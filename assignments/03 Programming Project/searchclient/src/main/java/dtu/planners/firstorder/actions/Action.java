package dtu.planners.firstorder.actions;

import dtu.planners.firstorder.effects.Effect;
import dtu.planners.firstorder.preconditions.Precondition;

import java.util.Collection;

public abstract class Action {

    private Collection<Precondition> preconditions;
    private Collection<Effect> effects;

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

    public abstract ActionType getType();
}
