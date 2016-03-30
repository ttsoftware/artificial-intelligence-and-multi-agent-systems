package dtu.agency.planners.actions;

public abstract class AbstractAction {

    protected int estimatedDistance;

    public abstract int getEstimatedDistance();

    public abstract AbstractActionType getType();
}
