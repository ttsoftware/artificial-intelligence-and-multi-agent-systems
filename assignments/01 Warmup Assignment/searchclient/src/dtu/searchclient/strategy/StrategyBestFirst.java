package dtu.searchclient.strategy;

import dtu.searchclient.Heuristic;
import dtu.searchclient.Node;

// Ex 3: Best-first Search uses a priority queue (Java contains no implementation of a Heap data structure)
public class StrategyBestFirst extends Strategy {

    private Heuristic heuristic;

    public StrategyBestFirst( Heuristic h ) {
        super();
        heuristic = h;
        // Unimplemented
    }
    public Node getAndRemoveLeaf() {
        // Unimplemented
        return null;
    }

    public void addToFrontier( Node n ) {
        // Unimplemented
    }

    public int countFrontier() {
        // Unimplemented
        return 0;
    }

    public boolean frontierIsEmpty() {
        // Unimplemented
        return true;
    }

    public boolean inFrontier( Node n ) {
        // Unimplemented
        return false;
    }

    public String toString() {
        return "Best-first Search (PriorityQueue) using " + heuristic.toString();
    }
}