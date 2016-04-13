package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.htn.HTNNode;

public class GreedyHTNNodeComparator extends HTNNodeComparator {

    public GreedyHTNNodeComparator(Heuristic heuristic) {
        super(heuristic);
    }

    public int f(HTNNode node) {
        return h(node);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
