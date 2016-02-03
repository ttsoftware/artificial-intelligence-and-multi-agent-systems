package dtu.searchclient.strategy;

import dtu.searchclient.Node;

public class StrategyDFS extends Strategy {
    public StrategyDFS() {
        super();
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
        return "Depth-first Search";
    }
}