package dtu.agency.planners.actions;

public class GotoAction extends AbstractAction {

    public GotoAction(int estimatedDistance, int row, int column) {
        this.estimatedDistance = estimatedDistance;
    }

    @Override
    public int getEstimatedDistance() {
        return estimatedDistance;
    }
}
