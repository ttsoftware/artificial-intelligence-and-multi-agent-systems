package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNNode;
import dtu.agency.planners.htn.heuristic.HTNNodeComparator;

import java.util.PriorityQueue;

/**
 * Best First Strategy
 */
public class BestFirstStrategy extends Strategy {

    private final HTNNodeComparator HTNNodeComparator;
    private final PriorityQueue<HTNNode> frontier;

    public BestFirstStrategy(HTNNodeComparator h) {
        super();
        HTNNodeComparator = h;
        frontier = new PriorityQueue<>(HTNNodeComparator);
    }

    public HTNNode getAndRemoveLeaf() {
        return frontier.poll();
    }

    public void addToFrontier(HTNNode n) {
        frontier.offer(n);
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
        return "Best-first Search (PriorityQueue) using " + HTNNodeComparator.toString();
    }
}