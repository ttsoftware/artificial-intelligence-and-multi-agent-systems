package dtu.agency.planners.htn.heuristic;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.rlaction.RLAction;
import dtu.agency.planners.htn.HTNNode;
import dtu.agency.services.PlanningLevelService;

import java.util.Comparator;
import java.util.List;

public abstract class HTNNodeComparator implements Comparator<HTNNode> {

    private final PlanningLevelService pls;
    HTNNodeComparator(PlanningLevelService pls) {
        this.pls = pls;
    }

    /**
     * TODO: We need a more descriptive name for 'h'
     * TODO: after each action, we need to approximate and update agent position in PlanningLevelService
     * TODO: AND have a way to revert it afterwards
     * TODO: maybe, we would only like to pass the position as argument, not the pls, and update it as we go along.
     * @param n
     * @return
     */
    public int h(HTNNode n) {
        int primitives = 0;
        List<Action> actions = n.getRemainingPlan().getActions();

        for (Action action : actions) {
            // count primitive actions, and refine all pure HLActions, getting a list of HLActions
            // as no pure HLPlan refines into other pure HLPlan, this is not walked like a tree,
            // this has to be corrected is such HLActions are introduced
            if (action instanceof ConcreteAction) {
                primitives += 1;
            }

            if (action instanceof SolveGoalAction) {
                SolveGoalAction sga = (SolveGoalAction) action;
                primitives += sga.approximateSteps(pls);
                // we need the box position as well here, but BDILevelService might be ok, as an approximation...?
            }

            if (action instanceof HLAction) {
                HLAction hla = (HLAction) action;
                primitives += hla.approximateSteps(pls);
            }

            if (action instanceof RLAction) {
                RLAction rla = (RLAction) action;
                primitives += rla.approximateSteps(pls);
            }
        }
        return primitives;
    }

    public int compare(HTNNode node1, HTNNode node2) {
        return f(node1) - f(node2);
    }

    /**
     * TODO: We need a more descriptive name for 'f'
     *
     * @param node
     * @return
     */
    protected abstract int f(HTNNode node);
}
