package dtu.agency.planners.plans;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.PlanningLevelService;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * High Level Plan stores a list of high level actions
 */
public class HLPlan implements AbstractPlan {

    private final LinkedList<HLAction> plan;

    public HLPlan() {
        this.plan = new LinkedList<>();
    }

    public HLAction poll() {
        return plan.pollFirst();
    }

    public HLAction peek() {
        return plan.peekFirst();
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
    public LinkedList<? extends AbstractAction> getActions() {
        return new LinkedList<>(plan);
    }

    /**
     * @param pls PlanningLevelService in state right before executing this plan (will not affected)
     * @return Return the approximate number of primitive actions this high level plan is going to take
     */
    public int approximateSteps(PlanningLevelService pls) {
        int approximateSteps = 0;

        for (HLAction action : plan) {
            approximateSteps += action.approximateSteps(pls);
            pls.apply(action);
        }

        return approximateSteps;
    }

    /**
     * @param pls PlanningLevelService in state right before executing this plan (will not affected)
     * @return the PrimitiveSteps that will turn this plan into reality
     */
    public PrimitivePlan evolve(PlanningLevelService pls) {
        RelaxationMode noAgents = RelaxationMode.NoAgents;

        HLAction first = poll();
        HTNPlanner htn = new HTNPlanner(pls, first, noAgents);
        PrimitivePlan plan = htn.plan();
        if (plan == null) {
            prepend(first);
            return null;
        }
        htn.commitPlan();

        Iterator actions = getActions().iterator();
        while (actions.hasNext()) {
            HLAction next = (HLAction) actions.next();

            htn.reload(next, noAgents);
            PrimitivePlan primitives = htn.plan();
            if (primitives==null) {
                prepend(first);
                // pls.revertLast(actionsCommitted);
                return null;
            }
            plan.appendActions(primitives);
            htn.commitPlan();
        }

        // return pls and plan to the state before this method
        prepend(first);
        return plan;
    }

    @Override
    public String toString() {
        return "HLPlan:" + getActions().toString();
    }

    public void extend(HLPlan hlPlan) {
        Iterator actions = hlPlan.getActions().listIterator();
        while (actions.hasNext()) {
            HLAction action = (HLAction) actions.next();
            plan.addLast(action);
        }
    }
}
