package dtu.agency.planners.actions;

public class MoveBoxAction extends AbstractAction {

    public MoveBoxAction(int estimatedDistance, int row, int column) {
        this.estimatedDistance = estimatedDistance;
    }

    @Override
    public int getEstimatedDistance() {
        return estimatedDistance;
    }
}
