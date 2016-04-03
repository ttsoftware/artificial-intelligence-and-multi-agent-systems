package dtu.agency.planners.htn.heuristic;


import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;

public class WeightedAStarHeuristic extends Heuristic {

    private int W;

    public WeightedAStarHeuristic(HTNState initialEffect, Box targetBox, Goal targetGoal, int weight) {
        super(initialEffect, targetBox, targetGoal);
        W = weight;
    }
    public int f(HTNNode n) {
        return n.g() + W * h(n);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", W);
    }
}
