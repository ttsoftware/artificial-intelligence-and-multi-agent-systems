package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class WeightedAStarHeuristicComparator extends HeuristicComparator {

    private int weight;

    public WeightedAStarHeuristicComparator(Heuristic heuristic, int weight) {
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
