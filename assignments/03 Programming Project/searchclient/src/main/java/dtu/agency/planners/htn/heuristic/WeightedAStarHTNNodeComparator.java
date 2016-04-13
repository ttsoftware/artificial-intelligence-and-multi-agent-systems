package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.htn.HTNNode;

public class WeightedAStarHTNNodeComparator extends HTNNodeComparator {

    private int weight;

    public WeightedAStarHTNNodeComparator(Heuristic heuristic, int weight) {
        super(heuristic);
        this.weight = weight;
    }

    public int f(HTNNode node) {
        return node.getGeneration() + weight * h(node);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", weight);
    }
}
