package dtu.agency.planners.htn.heuristic;

import dtu.searchclient.Node;
import dtu.searchclient.heuristic.Heuristic;

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
