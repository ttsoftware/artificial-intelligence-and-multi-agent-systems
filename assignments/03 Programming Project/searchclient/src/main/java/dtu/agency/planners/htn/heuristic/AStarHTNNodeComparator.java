package dtu.agency.planners.htn.heuristic;

import dtu.agency.planners.htn.HTNNode;
import dtu.agency.services.PlanningLevelService;

public class AStarHTNNodeComparator extends HTNNodeComparator {

    public AStarHTNNodeComparator(PlanningLevelService pls) {
        super(pls);
    }

    public int f(HTNNode node) {
        return node.getGeneration() + h(node);
    }

    public String toString() {
        return "A* evaluation";
    }
}
