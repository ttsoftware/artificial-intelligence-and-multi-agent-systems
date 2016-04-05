package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNNode;
import dtu.agency.planners.htn.heuristic.Heuristic;

import java.util.PriorityQueue;

public class BestFirstStrategy extends Strategy {

    private Heuristic heuristic;
    private PriorityQueue<HTNNode> frontier;

    public BestFirstStrategy(Heuristic h) {
        super();
        heuristic = h;
        frontier = new PriorityQueue<>(heuristic);
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
        return "Best-first Search (PriorityQueue) using " + heuristic.toString();
    }
}