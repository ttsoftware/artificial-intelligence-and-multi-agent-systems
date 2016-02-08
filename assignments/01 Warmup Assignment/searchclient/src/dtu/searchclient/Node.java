package dtu.searchclient;

import java.util.*;
import java.util.function.Predicate;

import dtu.searchclient.Command.dir;
import dtu.searchclient.Command.type;
import javafx.util.Pair;

public class Node {

    private static Random rnd = new Random(1);
    public static int maxRow;
    public static int maxColumn;

    private int agentRow;
    private int agentCol;

    // Arrays are indexed from the top-left of the level, with first index being row and second being column.
    // Row 0: (0,0) (0,1) (0,2) (0,3) ...
    // Row 1: (1,0) (1,1) (1,2) (1,3) ...
    // Row 2: (2,0) (2,1) (2,2) (2,3) ...
    // ...
    // (Start in the top left corner, first go down, then go right)
    // E.g. walls[2] is an array of booleans having size MAX_GRID
    // walls[row][col] is true if there's a wall at (row, col)
    //

    public static boolean[][] walls;
    public static char[][] goals;
    public static int boxCount;
    private char[][] boxes;

    private List<Pair<Integer, Integer>> boxesLocations = new ArrayList<>();

    // We store the goal locations in a list for faster iteration
    public static List<Pair<Integer, Integer>> goalLocations = new ArrayList<>();

    private Node parent;
    private Command action;

    private int g;

    public Node(Node parent) {
        this.parent = parent;

        boxes = new char[maxRow][maxColumn];

        if (parent == null) {
            g = 0;
        } else {
            g = parent.g() + 1;
        }
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

    public ArrayList<Node> getExpandedNodes() {
        ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.every.length);
        for (Command c : Command.every) {
            // Determine applicability of action
            int newAgentRow = this.agentRow + dirToRowChange(c.dir1);
            int newAgentCol = this.agentCol + dirToColChange(c.dir1);

            if (c.actType == type.Move) {
                // Check if there's a wall or box on the cell to which the agent is moving
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    Node n = this.ChildNode();
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
                        Node n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
                        n.boxes[newAgentRow][newAgentCol] = 0;

                        // update the boxes list
                        n.boxesLocations.add(new Pair<>(newBoxRow, newBoxCol));
                        n.boxesLocations.removeIf(location -> {
                            return location.getKey() == newAgentRow && location.getValue() == newAgentCol;
                        });

                        expandedNodes.add(n);
                    }
                }
            } else if (c.actType == type.Pull) {
                // Cell is free where agent is going
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = this.agentRow + dirToRowChange(c.dir2);
                    int boxCol = this.agentCol + dirToColChange(c.dir2);
                    // .. and there's a box in "dir2" of the agent
                    if (boxAt(boxRow, boxCol)) {
                        Node n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[this.agentRow][this.agentCol] = this.boxes[boxRow][boxCol];
                        n.boxes[boxRow][boxCol] = 0;

                        // update the boxes list
                        n.boxesLocations.add(new Pair<>(agentRow, agentCol));
                        n.boxesLocations.removeIf(location -> {
                            return location.getKey() == boxRow && location.getValue() == boxCol;
                        });

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

    public static boolean goalAt(int row, int col) {
        return goals[row][col] > 0;
    }

    private int dirToRowChange(dir d) {
        return (d == dir.S ? 1 : (d == dir.N ? -1 : 0)); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange(dir d) {
        return (d == dir.E ? 1 : (d == dir.W ? -1 : 0)); // East is left one column (1), west is right one column (-1)
    }

    private Node ChildNode() {
        Node copy = new Node(this);
        for (int row = 0; row < maxRow; row++) {
            System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, maxColumn);
            copy.setBoxesLocations(this.boxesLocations);
        }
        return copy;
    }

    public LinkedList<Node> extractPlan() {
        LinkedList<Node> plan = new LinkedList<Node>();
        Node n = this;
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
        Node other = (Node) obj;
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

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Command getAction() {
        return action;
    }

    public void setAction(Command action) {
        this.action = action;
    }

    public List<Pair<Integer, Integer>> getBoxesLocations() {
        return boxesLocations;
    }

    public void setBoxesLocations(List<Pair<Integer, Integer>> boxesLocations) {
        this.boxesLocations = boxesLocations;
    }

    public void addBoxLocation(Pair<Integer, Integer> location) { boxesLocations.add(location); }
}