package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.HTNNode;
import dtu.agency.planners.htn.Memory;

import java.util.HashSet;

public abstract class Strategy {

    public HashSet<HTNState> explored = new HashSet<>();

    public long startTime = System.currentTimeMillis();

    public Strategy() {
    }

    public void addToExplored(HTNState n) {
        explored.add(n);
    }

    public HashSet<HTNState> getExplored() {
        return explored;
    }

    public boolean isExplored(HTNState n) {
        return explored.contains(n);
    }

    public int countExplored() {
        return explored.size();
    }

    public String status() {
        return String.format("#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep());
    }

    public float timeSpent() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public abstract HTNNode getAndRemoveLeaf();

    public abstract void addToFrontier(HTNNode n);

    public abstract boolean inFrontier(HTNNode n);

    public abstract int countFrontier();

    public abstract boolean frontierIsEmpty();

    public abstract String toString();
}