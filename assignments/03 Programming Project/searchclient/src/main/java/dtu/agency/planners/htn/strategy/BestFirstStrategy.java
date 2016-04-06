package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNNode;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;

import java.util.PriorityQueue;

public class BestFirstStrategy extends Strategy {

    private HeuristicComparator heuristicComparator;
    private PriorityQueue<HTNNode> frontier;

    public BestFirstStrategy(HeuristicComparator h) {
        super();
        heuristicComparator = h;
        frontier = new PriorityQueue<>(heuristicComparator);
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
        return "Best-first Search (PriorityQueue) using " + heuristicComparator.toString();
    }
}