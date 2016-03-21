package dtu.agency.agent.actions;

public abstract class Action {

    public abstract ActionType getType();

    @Override
    public abstract String toString();
}
