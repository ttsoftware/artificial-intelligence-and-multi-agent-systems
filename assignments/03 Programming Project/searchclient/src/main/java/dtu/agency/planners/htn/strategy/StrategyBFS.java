package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNNode;

import java.util.ArrayDeque;

/**
 * Breadth First Strategy
 */
public class StrategyBFS extends Strategy {

    private ArrayDeque<HTNNode> frontier;

    public StrategyBFS() {
        super();
        frontier = new ArrayDeque<>();
    }

    public HTNNode getAndRemoveLeaf() {
        return frontier.pollFirst();
    }

    public void addToFrontier(HTNNode n) {
        frontier.addLast(n);
    }

    public int countFrontier() {
        return frontier.size();
    }

    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }

    public boolean inFrontier(HTNNode n) {
        return frontier.contains(n);
    }

    public String toString() {
        return "Breadth-first Search";
    }
}