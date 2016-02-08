package dtu.searchclient.heuristic;


import dtu.searchclient.Node;

public class WeightedAStarHeuristic extends Heuristic {

    private int W;

    public WeightedAStarHeuristic(Node initialState) {
        super(initialState);
        // You're welcome to test this out with different values,
        // but for the reporting part you must at least indicate benchmarks for W = 5
        W = 17;
    }

    public int f(Node n) {
        return n.g() + W * h(n);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", W);
    }
}
