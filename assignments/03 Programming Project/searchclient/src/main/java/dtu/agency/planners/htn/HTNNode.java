package dtu.agency.planners.htn;

import dtu.agency.agent.actions.Action;
import dtu.agency.planners.AbstractPlan;
import dtu.agency.planners.MixedPlan;
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
    private MixedPlan abPlan; // list of successive (abstract) actions

    private int g; // generation - how many ancestors exist? -> how many moves have i performed

    public HTNNode(HTNNode parent, Action action, HTNEffect initialEffects, MixedPlan abstractPlan) {
        this.parent = parent;
        this.action = action;
        this.effect = initialEffects;
        this.abPlan = abstractPlan;

        g = (parent == null) ? 0 : parent.g+1;

    }

    public int g() {
        return g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        for (int row = 1; row < maxRow - 1; row++) {
            for (int col = 1; col < maxColumn - 1; col++) {
                char g = goals[row][col];
                char b = Character.toLowerCase(boxes[row][col]);
                if (g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<HTNNode> getExpandedNodes() {
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
    }

    public boolean cellIsFree(int row, int col) {
        return (!walls[row][col] && this.boxes[row][col] == 0);
    }

    public boolean boxAt(int row, int col) {
        return this.boxes[row][col] > 0;
    }

    private int dirToRowChange(dir d) {
        return (d == dir.S ? 1 : (d == dir.N ? -1 : 0)); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange(dir d) {
        return (d == dir.E ? 1 : (d == dir.W ? -1 : 0)); // East is left one column (1), west is right one column (-1)
    }

    private HTNNode ChildNode() {
        HTNNode copy = new HTNNode(this);
        for (int row = 0; row < maxRow; row++) {
            System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, maxColumn);
        }
        return copy;
    }

    public LinkedList<HTNNode> extractPlan() {
        LinkedList<HTNNode> plan = new LinkedList<HTNNode>();
        HTNNode n = this;
        while (!n.isInitialState()) {
            plan.addFirst(n);
            n = n.parent;
        }
        return plan;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentCol;
        result = prime * result + agentRow;
        result = prime * result + Arrays.deepHashCode(boxes);
        result = prime * result + Arrays.deepHashCode(goals);
        result = prime * result + Arrays.deepHashCode(walls);
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
        if (agentCol != other.agentCol)
            return false;
        if (agentRow != other.agentRow)
            return false;
        if (!Arrays.deepEquals(boxes, other.boxes)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < maxRow; row++) {
            if (!walls[row][0]) {
                break;
            }
            for (int col = 0; col < maxColumn; col++) {
                if (this.boxes[row][col] > 0) {
                    s.append(this.boxes[row][col]);
                } else if (goals[row][col] > 0) {
                    s.append(goals[row][col]);
                } else if (walls[row][col]) {
                    s.append("+");
                } else if (row == this.agentRow && col == this.agentCol) {
                    s.append("0");
                } else {
                    s.append(" ");
                }
            }

            s.append("\n");
        }
        return s.toString();
    }

    public char[][] getBoxes() {
        return boxes;
    }

    public int getAgentRow() {
        return agentRow;
    }

    public void setAgentRow(int agentRow) {
        this.agentRow = agentRow;
    }

    public int getAgentCol() {
        return agentCol;
    }

    public void setAgentCol(int agentCol) {
        this.agentCol = agentCol;
    }

    public HTNNode getParent() {
        return parent;
    }

    public void setParent(HTNNode parent) {
        this.parent = parent;
    }

    public Command getAction() {
        return action;
    }

    public void setAction(Command action) {
        this.action = action;
    }
}