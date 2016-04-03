package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;

public class AStarHeuristic extends Heuristic {

    public AStarHeuristic(HTNState initialEffect, Box targetBox, Goal targetGoal) {
        super(initialEffect, targetBox, targetGoal);
    }

    public int f(HTNNode n) {
        return n.g() + h(n);
    }

    public String toString() {
        return "A* evaluation";
    }
}
