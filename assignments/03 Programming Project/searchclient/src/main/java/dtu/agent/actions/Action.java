package dtu.agent.actions;

import dtu.agent.actions.effects.Effect;
import dtu.agent.actions.preconditions.Precondition;
import dtu.planners.actions.AbstractAction;

import java.util.Collection;

public abstract class Action extends AbstractAction {

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
