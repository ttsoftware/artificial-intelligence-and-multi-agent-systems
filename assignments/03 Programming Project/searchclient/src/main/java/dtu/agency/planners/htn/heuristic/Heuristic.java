package dtu.agency.planners.htn.heuristic;

import dtu.agency.agent.actions.Action;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Heuristic implements Comparator<HTNNode> {

    /*
    public HTNState initialState;
    public HLAction action;
    public Box targetBox;
    public Goal targetGoal;

    public Position initialPosition;
    public List<AbstractAction> actions;
    */

    public Method method = Method.MANHATTAN;

    /*
    public Heuristic(HTNState initialEffect, Box targetBox, Goal targetGoal) {
        this.initialState = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
    }

    public Heuristic(HTNState initialEffect, Box targetBox, Goal targetGoal, Method method) {
        this.initialState = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
        this.method = method;
    }

    // a new constructor is needed, taking a single HLAction, along with initial state, the rest must be found from this
    public Heuristic(Position initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Heuristic(HTNNode node) {
        this.initialState = node.getState();
        this.actions = node.getRemainingPlan().getActions();
    }*/

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
        ArrayList<HLAction> midLevelActions = new ArrayList<>();
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

    /*
    public int manhattan(HTNNode n) {
        // this has to be improved somewhat: main idea is the following...
        // refine all 'pure' HLActions, and concatenate plan
        // for each entry in plan add previous destination to target destination, first on accepting initial HTNState.
        int distToBox = Math.abs(n.getState().getAgentPosition().manhattanDist(targetBox.getPosition()) - 1) ; //next to is better
        int distToGoal = n.getState().getBoxPosition().manhattanDist(targetGoal.getPosition());
        return distToBox + distToGoal;
    }

    public int euclidean(HTNNode n) {
        long distToBox  = Math.round(Math.abs(n.getState().getAgentPosition().eucDist(targetBox.getPosition()) - 1)) ; //next to is better
        long distToGoal = Math.round(n.getState().getBoxPosition().eucDist(targetGoal.getPosition()));
        return (int) (distToBox + distToGoal);
    }

    public int h(HTNNode n) {
        switch (this.method) {
            case MANHATTAN:
                return manhattan(n);
            case EUCLIDEAN:
                return euclidean(n);
        }

        throw new UnsupportedOperationException("Invalid direction object.");
    }
    */
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
