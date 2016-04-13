package dtu.agency.planners.agentplanner;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.AbstractPlan;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by koeus on 4/9/16.
 */
public class HLPlan implements AbstractPlan {

    private final LinkedList<HLAction> plan;

    public HLPlan() {
        this.plan = new LinkedList<>();
    }

    public HLPlan(HLAction action) {
        this.plan = new LinkedList<>();
        this.plan.add(action);
    }

    public HLPlan(Queue<HLAction> plan) {
        this.plan = new LinkedList<>(plan);
    }

    public LinkedList<HLAction> getPlan() {
        return new LinkedList<>(plan);
    }

    public HLAction poll() {
        return plan.pollFirst();
    }

    public void prepend(HLAction action) {
        plan.addFirst(action);
    }

    public void append(HLAction action) {
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

        for (HLAction action : plan) {
            approximateSteps += action.approximateSteps(nextOrigin);
            nextOrigin = action.getAgentDestination();
        }

        return approximateSteps;
    }

    public HLAction peek() {
        return plan.peekFirst();
    }
}
