package dtu.agency.planners.htn.heuristic;

import com.sun.xml.internal.bind.v2.TODO;
import dtu.agency.planners.htn.HTNNode;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<HTNNode> {

    public HTNNode initialNode;

    public Heuristic(HTNNode initialNode) {
        this.initialNode = initialNode;
    }

    public int compare(HTNNode n1, HTNNode n2) {
        return f(n1) - f(n2);
    }

    /**
     * Returns how far the target box are from it's goal
     */
    public int h(HTNNode n) {
        // we wish to calculate the distance the target box is from the goal
        TODO
        n.getAction();
    }


    public abstract int f(HTNNode n);
}
