package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.planners.htn.HTNNode;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<HTNNode> {

    public HTNEffect initialEffect;
    public Box targetBox;
    public Goal targetGoal;
    public Method method = Method.MANHATTAN;


    public Heuristic(HTNEffect initialEffect, Box targetBox, Goal targetGoal) {
        this.initialEffect = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
    }

    public Heuristic(HTNEffect initialEffect, Box targetBox, Goal targetGoal, Method method) {
        this.initialEffect = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
        this.method = method;
    }

    public int compare(HTNNode n1, HTNNode n2) {
        return f(n1) - f(n2);
    }

    public int manhattan(HTNNode n) {
        int distToBox = Math.abs(n.getEffect().getAgentPosition().manhattanDist(targetBox.getPosition()) - 1) ; //next to is better
        int distToGoal = n.getEffect().getBoxPosition().manhattanDist(targetGoal.getPosition());
        return distToBox + distToGoal;
    }

    public int euclidean(HTNNode n) {
        long distToBox  = Math.round(Math.abs(n.getEffect().getAgentPosition().eucDist(targetBox.getPosition()) - 1)) ; //next to is better
        long distToGoal = Math.round(n.getEffect().getBoxPosition().eucDist(targetGoal.getPosition()));
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
