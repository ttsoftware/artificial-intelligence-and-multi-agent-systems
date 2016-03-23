package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.planners.htn.HTNNode;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<HTNNode> {

    public HTNEffect initialEffect;
    public Box targetBox;
    public Goal targetGoal;

    public Heuristic(HTNEffect initialEffect, Box targetBox, Goal targetGoal) {
        this.initialEffect = initialEffect;
        this.targetGoal = targetGoal;
        this.targetBox = targetBox;
    }

    public int compare(HTNNode n1, HTNNode n2) {
        return f(n1) - f(n2);
    }

    /**
     * Returns how far the agent is from the target box, and how far the target box are from it's goal.
     */
    public int h(HTNNode n) {
        int distToBox = Math.abs(n.getEffect().getAgentPosition().manhattanDist(targetBox.getPosition()) - 1) ; //next to is better
        int distToGoal = n.getEffect().getBoxPosition().manhattanDist(targetGoal.getPosition());
        return distToBox + distToGoal;
    }

    public abstract int f(HTNNode n);
}
