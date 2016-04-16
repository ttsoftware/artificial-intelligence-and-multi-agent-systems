package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;
import dtu.agency.services.PlanningLevelService;

public class WeightedAStarHTNNodeComparator extends HTNNodeComparator {

    private int weight;

    public WeightedAStarHTNNodeComparator(PlanningLevelService pls, int weight) {
        super(pls);
        this.weight = weight;
    }

    public int f(HTNNode node) {
        return node.getGeneration() + weight * h(node);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", weight);
    }
}
