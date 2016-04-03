package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;

public class GreedyHeuristic extends Heuristic {

    public GreedyHeuristic(HTNState initialEffect, Box targetBox, Goal targetGoal) {
        super(initialEffect, targetBox, targetGoal);
    }

    public int f(HTNNode n) {
        return h(n);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
