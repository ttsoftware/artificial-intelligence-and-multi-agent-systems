package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.htn.HTNNode;
import dtu.agency.services.PlanningLevelService;

public class GreedyHTNNodeComparator extends HTNNodeComparator {

    public GreedyHTNNodeComparator(PlanningLevelService pls) {
        super(pls);
    }

    public int f(HTNNode node) {
        return h(node);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
