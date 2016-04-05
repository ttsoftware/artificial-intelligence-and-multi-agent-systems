package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class GreedyHeuristicComparator extends HeuristicComparator {

    public GreedyHeuristicComparator(Heuristic heuristic) {
        super(heuristic);
    }

    public int f(HTNNode node) {
        return h(node);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
