package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.htn.HTNNode;

public class AStarHTNNodeComparator extends HTNNodeComparator {

    public AStarHTNNodeComparator(Heuristic heuristic) {
        super(heuristic);
    }

    public int f(HTNNode node) {
        return node.getGeneration() + h(node);
    }

    public String toString() {
        return "A* evaluation";
    }
}
