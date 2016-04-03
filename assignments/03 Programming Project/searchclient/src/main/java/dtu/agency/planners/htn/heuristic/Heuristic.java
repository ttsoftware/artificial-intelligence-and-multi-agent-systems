package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;
import java.util.Comparator;

public abstract class Heuristic implements Comparator<HTNNode> {

    public HTNState initialEffect;
    public HLAction action;
    public Box targetBox;
    public Goal targetGoal;
    public Method method = Method.MANHATTAN;


    public Heuristic(HTNState initialEffect, Box targetBox, Goal targetGoal) {
        this.initialEffect = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
    }

    public Heuristic(HTNState initialEffect, Box targetBox, Goal targetGoal, Method method) {
        this.initialEffect = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
        this.method = method;
    }

    /* a new constructor is needed, taking a single HLAction, along with initial state, the rest must be found from this
    public Heuristic(HTNState initialEffect, HLAction action) {
        this.initialEffect = initialEffect;
        this.action = action;
    }*/

    public int compare(HTNNode n1, HTNNode n2) {
        return f(n1) - f(n2);
    }

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

    public abstract int f(HTNNode n);
}
