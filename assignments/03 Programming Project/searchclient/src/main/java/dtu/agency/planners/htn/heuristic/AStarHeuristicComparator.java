package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class AStarHeuristicComparator extends HeuristicComparator {

    public AStarHeuristicComparator(Heuristic heuristic) {
        super(heuristic);
    }

    public int f(HTNNode node) {
        return node.getGeneration() + h(node);
    }

    public String toString() {
        return "A* evaluation";
    }
}
