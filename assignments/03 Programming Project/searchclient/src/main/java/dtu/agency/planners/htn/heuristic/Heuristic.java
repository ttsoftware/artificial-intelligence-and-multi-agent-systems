package dtu.agency.planners.htn.heuristic;

import dtu.agency.agent.actions.Action;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.htn.HTNNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Heuristic implements Comparator<HTNNode> {

    public Method method = Method.MANHATTAN;

    public Heuristic(Method method) {
        this.method = method;
    }

    public int compare(HTNNode n1, HTNNode n2) {
        return f(n1) - f(n2);
    }

    public int h(HTNNode n) {
        Position previous = n.getState().getAgentPosition();
        List<AbstractAction> actions = n.getRemainingPlan().getActions();
        int primitives = 0;
        for (AbstractAction action : actions) {
            // count primitive actions, and refine all pure HLActions, getting a list of HLActions
            // as no pure HLAction refines into other pure HLAction, this is not walked like a tree,
            // this has to be corrected is such HLActions are introduced
            if (action instanceof Action) {
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
                        for (AbstractAction a : plan.getActions()) {
                            if (a instanceof Action) {
                                planPrimitives += 1;
                            } else { // impure actions! by previous definition - else this is to be rewritten
                                HLAction a_ = (HLAction) a;
                                planPrimitives += distance(planPrevious, a_.getDestination());
                                planPrevious = act.getDestination();
                            }
                        }
                        minPlanPrimitives = (minPlanPrimitives > planPrimitives) ? planPrimitives : minPlanPrimitives;
                        minPlanPrevious = (minPlanPrimitives > planPrimitives) ? planPrevious : minPlanPrevious;
                    }
                    previous = minPlanPrevious;
                    primitives += minPlanPrimitives;
                } else { // 'impure' HLAction
                    primitives += distance(previous, act.getDestination());
                    previous = act.getDestination();
                }
            }
        }
        return primitives;
    }

    private int distance(Position from, Position to) {
        int dist = 0;
        switch (method) {
            case MANHATTAN:
                dist = from.manhattanDist(to);
                break;
            case EUCLIDEAN:
                dist = (int) Math.round(from.eucDist(to));
                break;
        }
        return dist;
    }

    public abstract int f(HTNNode n);
}
