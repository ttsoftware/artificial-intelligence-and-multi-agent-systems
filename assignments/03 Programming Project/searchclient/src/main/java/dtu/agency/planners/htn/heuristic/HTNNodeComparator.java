package dtu.agency.planners.htn.heuristic;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.rlaction.RLAction;
import dtu.agency.board.Position;
import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.planners.htn.HTNNode;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class HTNNodeComparator implements Comparator<HTNNode> {

    private final PlanningLevelService pls;
    HTNNodeComparator(PlanningLevelService pls) {
        this.pls = pls;
    }

    /**
     * TODO: We need a more descriptive name for 'h'
     *
     * @param n
     * @return
     */
    public int h(HTNNode n) {
        Position previous = n.getState().getAgentPosition();
        List<Action> actions = n.getRemainingPlan().getActions();
        int primitives = 0;

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
//                primitives += 10;
            }

            if (action instanceof HLAction) {
                HLAction hla = (HLAction) action;
                primitives += hla.approximateSteps(pls);
//                primitives += 10;
            }

            if (action instanceof RLAction) {
                RLAction rla = (RLAction) action;
                primitives += rla.approximateSteps(pls);
//                primitives += 4;
            }
        }
        return primitives;
    }

//            if (action instanceof RLAction) {
//                HLAction recursiveAction = (RLAction) action;
//                primitives += heuristic.distance(previous, recursiveAction.getAgentDestination());
//                previous = recursiveAction.getAgentDestination();
//
//            } else {
//                if (action instanceof HLAction) { // (action instanceof HLAction)
//
//                    HLAction hlAction = (HLAction) action;
//                    ArrayList<MixedPlan> plans = n.getState().getRefinements(hlAction);
//
//                    int minPlanPrimitives = Integer.MAX_VALUE;
//                    Position minPlanPrevious = null;
//
//                    for (MixedPlan plan : plans) { // write recursive function to make this work
//                        int planPrimitives = 0;
//                        Position planPrevious = previous;
//
//                        for (Action action1 : plan.getActions()) {
//                            if (action1 instanceof ConcreteAction) {
//                                planPrimitives += 1;
//                            }
//                            if (action1 instanceof RLAction) {
//                                RLAction rlAction1 = (RLAction) action1;
//                                planPrimitives += heuristic.distance(planPrevious, rlAction1.getAgentDestination());
//                                planPrevious = hlAction.getAgentDestination();
//                            } else {
//                                HLAction hlAction1 = (HLAction) action1;
//                                planPrimitives += heuristic.distance(planPrevious, hlAction1.getAgentDestination());
//                                planPrevious = hlAction.getAgentDestination();
//                            }
//                        }
//                        minPlanPrimitives = (minPlanPrimitives > planPrimitives) ? planPrimitives : minPlanPrimitives;
//                        minPlanPrevious = (minPlanPrimitives > planPrimitives) ? planPrevious : minPlanPrevious;
//                    }
//                    previous = minPlanPrevious;
//                    primitives += minPlanPrimitives;
//                }
//            }
//        }
//        return primitives;
//    }

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
