package dtu.agency.planners.htn.heuristic;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.htn.HTNNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class HeuristicComparator implements Comparator<HTNNode> {

    public Heuristic heuristic;

    public HeuristicComparator(Heuristic heuristic) {
        this.heuristic = heuristic;
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
            // as no pure HLAction refines into other pure HLAction, this is not walked like a tree,
            // this has to be corrected is such HLActions are introduced
            if (action instanceof ConcreteAction) {
                primitives += 1;
            } else { // (action instanceof HLAction)
                HLAction act = (HLAction) action;
                if (act.isPureHLAction()) {
                    ArrayList<MixedPlan> plans = ((HLAction) action).getRefinements(n.getState());
                    int minPlanPrimitives = Integer.MAX_VALUE;
                    Position minPlanPrevious = null;
                    for (MixedPlan plan : plans) {
                        int planPrimitives = 0;
                        Position planPrevious = previous;
                        for (Action a : plan.getActions()) {
                            if (a instanceof ConcreteAction) {
                                planPrimitives += 1;
                            } else { // impure actions! by previous definition - else this is to be rewritten
                                HLAction a_ = (HLAction) a;
                                planPrimitives += heuristic.distance(planPrevious, a_.getDestination());
                                planPrevious = act.getDestination();
                            }
                        }
                        minPlanPrimitives = (minPlanPrimitives > planPrimitives) ? planPrimitives : minPlanPrimitives;
                        minPlanPrevious = (minPlanPrimitives > planPrimitives) ? planPrevious : minPlanPrevious;
                    }
                    previous = minPlanPrevious;
                    primitives += minPlanPrimitives;
                } else { // 'impure' HLAction
                    primitives += heuristic.distance(previous, act.getDestination());
                    previous = act.getDestination();
                }
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
    public abstract int f(HTNNode node);
}
