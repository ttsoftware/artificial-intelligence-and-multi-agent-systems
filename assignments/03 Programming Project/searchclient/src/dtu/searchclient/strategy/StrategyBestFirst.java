package dtu.searchclient.strategy;

import dtu.searchclient.Node;
import dtu.searchclient.heuristic.Heuristic;

import java.util.PriorityQueue;

public class StrategyBestFirst extends Strategy {

    private Heuristic heuristic;
    private PriorityQueue<Node> frontier;

    public StrategyBestFirst(Heuristic h) {
        super();
        heuristic = h;
        frontier = new PriorityQueue<>(heuristic);
    }

    public Node getAndRemoveLeaf() {
        return frontier.poll();
    }

    public void addToFrontier(Node n) {
        frontier.offer(n);
    }

    public int countFrontier() {
        return frontier.size();
    }

    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }

    public boolean inFrontier(Node n) {
        return frontier.contains(n);
    }

    public String toString() {
        return "Best-first Search (PriorityQueue) using " + heuristic.toString();
    }
}