package dtu.searchclient.heuristic;

import dtu.searchclient.Node;

public class AStarHeuristic extends Heuristic {

    public AStarHeuristic(Node initialState) {
        super(initialState);
    }

    public int f(Node n) {
        return n.g() + h(n);
    }

    public String toString() {
        return "A* evaluation";
    }
}
