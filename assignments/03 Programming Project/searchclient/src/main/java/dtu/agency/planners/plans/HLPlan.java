package dtu.agency.planners.plans;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.PlanningLevelService;

import java.util.Iterator;
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

    public HLPlan(HLAction action) {
        this.plan = new LinkedList<>();
        this.plan.add(action);
    }

    public HLPlan(Queue<HLAction> plan) {
        this.plan = new LinkedList<>(plan);
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
    public List<? extends AbstractAction> getActions() {
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

        // return pls and plan to the state before this method
        pls.revertLast(plan.size());

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
        int actionsCommitted = 1;

        Iterator actions = getActions().iterator();
        while (actions.hasNext()) {
            HLAction next = (HLAction) actions.next();

            htn.reload(next, noAgents);
            PrimitivePlan primitives = htn.plan();
            if (primitives==null) {
                prepend(first);
                pls.revertLast(actionsCommitted);
                return null;
            }
            plan.appendActions(primitives);
            htn.commitPlan();
            actionsCommitted += 1;
        }

        // return pls and plan to the state before this method
        prepend(first);
        pls.revertLast(actionsCommitted);
        return plan;
    }

    @Override
    public String toString() {
        return "HLPlan:" + getActions().toString();
    }

}
