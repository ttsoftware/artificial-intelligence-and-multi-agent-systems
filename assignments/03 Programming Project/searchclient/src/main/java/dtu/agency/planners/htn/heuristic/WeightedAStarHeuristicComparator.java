package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;

public class WeightedAStarHeuristicComparator extends HeuristicComparator {

    private int W;

    public WeightedAStarHeuristicComparator(Method method, int weight) {
        super(method);
        W = weight;
    }

    public int f(HTNNode n) {
        return n.g() + W * h(n);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", W);
    }
}
