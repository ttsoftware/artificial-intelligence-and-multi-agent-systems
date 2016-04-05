package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class AStarHeuristic extends Heuristic {

    public AStarHeuristic(Method method) {
        super(method);
    }

    public int f(HTNNode n) {
        return n.g() + h(n);
    }

    public String toString() {
        return "A* evaluation";
    }
}
