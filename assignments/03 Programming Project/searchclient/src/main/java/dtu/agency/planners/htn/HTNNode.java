package dtu.agency.planners.htn;

import dtu.agency.agent.actions.Action;
import dtu.agency.board.Level;
import dtu.agency.planners.AbstractPlan;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.searchclient.Command;
import dtu.searchclient.Command.dir;
import dtu.searchclient.Command.type;
import javafx.util.Pair;

import java.util.*;

public class HTNNode {

    // REALLY NEEDS SOME ATTENTION - SHOULD BE REVISITED FROM START TO END!
    // Builds on Node from SearchClient, which is quite unlike what this should be like...


    // public static Map<HTNEffect,boolean> visitedEffects = new HashMap<>(); // in strategy instead?!

    private static Random rnd = new Random(1);

    private HTNNode parent;
    private Action action;   // primitive action represented by this node
    private HTNEffect effect; // status of the relevant board features
    private MixedPlan remainingActions; // list of successive (abstract) actions

    private int g; // generation - how many ancestors exist? -> how many moves have i performed

    public HTNNode(HTNNode parent, Action action, HTNEffect initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.action = action;
        this.effect = initialEffects;
        this.remainingActions = highLevelPlan;
        this.g = (parent == null) ? 0 : (parent.g + 1);
    }



    public int g() {
        return g;
    }

    public boolean isInitialNode() {
        return this.parent == null;
    }

    public ArrayList<HTNNode> getRefinementNodes(Level level) {

        if (this.remainingActions.isEmpty()) {return new ArrayList<>();}

        // old stuff for inspiration
        /*
        ArrayList<HTNNode> expandedNodes = new ArrayList<HTNNode>(Command.every.length);
        for (Command c : Command.every) {
            // Determine applicability of action
            int newAgentRow = this.agentRow + dirToRowChange(c.dir1);
            int newAgentCol = this.agentCol + dirToColChange(c.dir1);

            if (c.actType == type.Move) {
                // Check if there's a wall or box on the cell to which the agency is moving
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    HTNNode n = this.ChildNode();
                    n.action = c;
                    n.agentRow = newAgentRow;
                    n.agentCol = newAgentCol;
                    expandedNodes.add(n);
                }
            } else if (c.actType == type.Push) {
                // Make sure that there's actually a box to move
                if (boxAt(newAgentRow, newAgentCol)) {
                    int newBoxRow = newAgentRow + dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + dirToColChange(c.dir2);

                    // .. and that new cell of box is free
                    if (cellIsFree(newBoxRow, newBoxCol)) {
                        HTNNode n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
                        n.boxes[newAgentRow][newAgentCol] = 0;

                        expandedNodes.add(n);
                    }
                }
            } else if (c.actType == type.Pull) {
                // Cell is free where agency is going
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = this.agentRow + dirToRowChange(c.dir2);
                    int boxCol = this.agentCol + dirToColChange(c.dir2);
                    // .. and there's a box in "dir2" of the agency
                    if (boxAt(boxRow, boxCol)) {
                        HTNNode n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[this.agentRow][this.agentCol] = this.boxes[boxRow][boxCol];
                        n.boxes[boxRow][boxCol] = 0;

                        expandedNodes.add(n);
                    }
                }
            }
        }
        Collections.shuffle(expandedNodes, rnd);
        return expandedNodes;
        */
    }


    private HTNNode ChildNode(Action primitiveAction, MixedPlan remainingActions) {
        HTNEffect oldState = this.getEffect();
        HTNEffect newState = primitiveAction.applyTo(oldState);

        // maybe postpone this check until HTNPlanner picks the Node??
        // Then we will have the level available
        boolean valid = true;
        valid &= level.notWall(newState.getAgentPosition());
        valid &= level.notWall(newState.getBoxPosition());
        if (!valid) return null;

        return new HTNNode(this, primitiveAction, newState, remainingActions);
    }

    public PrimitivePlan extractPlan() {
        LinkedList<Action> plan = new LinkedList<>();
        HTNNode n = this;
        Action previous;
        while (!n.isInitialNode()) {
            previous = n.getAction();
            if (previous != null) plan.addFirst(previous);
            n = n.getParent();
        }
        return new PrimitivePlan(plan);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parent.hashCode();
        result = prime * result + action.hashCode();
        result = prime * result + effect.hashCode();
        result = prime * result + remainingActions.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNNode other = (HTNNode) obj;
        if (parent != other.parent)
            return false;
        if (action != other.action)
            return false;
        if (effect != other.effect)
            return false;
        if (!remainingActions.equals(other.remainingActions)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("HTNNode: \n");
        s.append("Level :");
        s.append(Integer.toString(this.g));
        s.append("\n");
        s.append("Action :");
        s.append(this.action.toString());
        s.append("\n");
        s.append("Effect :");
        s.append(this.effect.toString());
        s.append("\n");
        s.append("RemainingActions :");
        s.append(this.remainingActions.toString());
        s.append("\n");
        return s.toString();
    }



    public HTNNode getParent() {
        return parent;
    }

    public void setParent(HTNNode parent) {
        this.parent = parent;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public HTNEffect getEffect() {
        return effect;
    }

    public void setEffect(HTNEffect effect) {
        this.effect = effect;
    }
}