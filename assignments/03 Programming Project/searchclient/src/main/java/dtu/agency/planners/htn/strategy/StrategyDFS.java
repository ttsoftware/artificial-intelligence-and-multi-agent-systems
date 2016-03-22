package dtu.agency.planners.htn.strategy;

import dtu.agency.planners.htn.HTNNode;

import java.util.Stack;

public class StrategyDFS extends Strategy {

    private Stack<HTNNode> frontier;

    public StrategyDFS() {
        super();
        frontier = new Stack<>();
    }

    public HTNNode getAndRemoveLeaf() {
        return frontier.pop();
    }

    public void addToFrontier(HTNNode n) {
        frontier.push(n);
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
        return "Depth-first Search";
    }
}