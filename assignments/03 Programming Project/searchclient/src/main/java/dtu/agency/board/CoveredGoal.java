package dtu.agency.board;

public class CoveredGoal extends BoardObject {

    private String coveringObjectLabel;
    private String goalLabel;

    public CoveredGoal(String goalLabel, String coveringObjectLabel) {
        super(goalLabel);
        this.goalLabel = goalLabel;
        this.coveringObjectLabel = coveringObjectLabel;
    }

    public String getCoveringObjectLabel() {
        return coveringObjectLabel;
    }

    public void setCoveringObjectLabel(String coveringObjectLabel) {
        this.coveringObjectLabel = coveringObjectLabel;
    }

    public String getGoalLabel() {
        return goalLabel;
    }

    public void setGoalLabel(String goalLabel) {
        this.goalLabel = goalLabel;
    }
}