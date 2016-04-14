package dtu.agency.planners.plans;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.board.Position;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * High Level Plan stores a list of high level actions
 */
public class HLPlan implements AbstractPlan {

    private final LinkedList<HLAction> plan;

    public HLPlan() {
        this.plan = new LinkedList<>();
    }

    public HLPlan(dtu.agency.actions.abstractaction.hlaction.HLAction action) {
        this.plan = new LinkedList<>();
        this.plan.add(action);
    }

    public HLPlan(Queue<dtu.agency.actions.abstractaction.hlaction.HLAction> plan) {
        this.plan = new LinkedList<>(plan);
    }

    public LinkedList<dtu.agency.actions.abstractaction.hlaction.HLAction> getPlan() {
        return new LinkedList<>(plan);
    }

    public dtu.agency.actions.abstractaction.hlaction.HLAction poll() {
        return plan.pollFirst();
    }

    public void prepend(dtu.agency.actions.abstractaction.hlaction.HLAction action) {
        plan.addFirst(action);
    }

    public void append(dtu.agency.actions.abstractaction.hlaction.HLAction action) {
        plan.addLast(action);
    }

    public boolean isEmpty() {
        return plan.isEmpty();
    }

    @Override
    public List<? extends AbstractAction> getActions() {
        return new LinkedList<>(plan);
    }

    @Override
    public String toString() {
        return "HLPlan:" + getActions().toString();
    }

    public int approximateSteps(Position agentOrigin) {
        Position nextOrigin = agentOrigin;
        int approximateSteps = 0;

        for (dtu.agency.actions.abstractaction.hlaction.HLAction action : plan) {
            approximateSteps += action.approximateSteps(nextOrigin);
            nextOrigin = action.getAgentDestination();
        }

        return approximateSteps;
    }

    public dtu.agency.actions.abstractaction.hlaction.HLAction peek() {
        return plan.peekFirst();
    }
}
