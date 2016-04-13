package dtu.agency.planners.htn.heuristic;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.rlaction.RLAction;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.MixedPlan;
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
            }

            if (action instanceof RLAction) {
                HLAction recursiveAction = (RLAction) action;
                primitives += heuristic.distance(previous, recursiveAction.getAgentDestination());
                previous = recursiveAction.getAgentDestination();

            } else { // (action instanceof HLAction)

                HLAction hlAction = (HLAction) action;
                ArrayList<MixedPlan> plans = n.getState().getRefinements(hlAction);

                int minPlanPrimitives = Integer.MAX_VALUE;
                Position minPlanPrevious = null;

                for (MixedPlan plan : plans) { // write recursive function to make this work
                    int planPrimitives = 0;
                    Position planPrevious = previous;

                    for (Action action1 : plan.getActions()) {
                        if (action1 instanceof ConcreteAction) {
                            planPrimitives += 1;
                        }
                        if (action1 instanceof RLAction) {
                            RLAction rlAction1 = (RLAction) action1;
                            planPrimitives += heuristic.distance(planPrevious, rlAction1.getAgentDestination());
                            planPrevious = hlAction.getAgentDestination();
                        }
                        else {
                            HLAction hlAction1 = (HLAction) action1;
                            planPrimitives += heuristic.distance(planPrevious, hlAction1.getAgentDestination());
                            planPrevious = hlAction.getAgentDestination();
                        }
                    }
                    minPlanPrimitives = (minPlanPrimitives > planPrimitives) ? planPrimitives : minPlanPrimitives;
                    minPlanPrevious = (minPlanPrimitives > planPrimitives) ? planPrevious : minPlanPrevious;
                }
                previous = minPlanPrevious;
                primitives += minPlanPrimitives;
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
