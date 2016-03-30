package dtu.agency.planners.htn;

import dtu.agency.planners.AbstractPlan;
import dtu.agency.planners.actions.AbstractAction;

import java.util.List;

public class HTNPlan implements AbstractPlan {

    private List<AbstractAction> actions;

    public HTNPlan(List<AbstractAction> actions) {
        this.actions = actions;
    }

    @Override
    public List<AbstractAction> getActions() {
        return this.actions;
    }

    /**
     * @return Sum of estimated distances
     */
    @Override
    public int totalEstimatedDistance() {
        return actions.stream().mapToInt(AbstractAction::getEstimatedDistance).sum();
    }

    @Override
    public int getSteps() {
        return actions.size();
    }
}