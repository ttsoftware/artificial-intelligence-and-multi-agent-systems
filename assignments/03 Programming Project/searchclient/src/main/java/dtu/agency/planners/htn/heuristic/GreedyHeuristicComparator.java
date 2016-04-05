package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class GreedyHeuristicComparator extends HeuristicComparator {

    public GreedyHeuristicComparator(Method method) {
        super(method);
    }

    public int f(HTNNode n) {
        return h(n);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
