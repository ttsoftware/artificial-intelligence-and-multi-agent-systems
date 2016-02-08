package dtu.searchclient.heuristic;

import dtu.searchclient.Node;
import javafx.util.Pair;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<Node> {

    public Node initialState;

    public Heuristic(Node initialState) {
        this.initialState = initialState;
    }

    public int compare(Node n1, Node n2) {
        return f(n1) - f(n2);
    }

    /**
     * Returns how far each box is from their closest goals
     *
     * @param n
     * @return
     */
    public int h(Node n) {
        // we wish to calculate the distance the boxes have from the goal
        char[][] boxes = n.getBoxes();

        int totalDistance = 0;

        for (int boxesRow = 0; boxesRow < boxes.length; boxesRow++) {
            for (int boxesCol = 0; boxesCol < boxes[boxesRow].length; boxesCol++) {
                if (n.boxAt(boxesRow, boxesCol)) {
                    // we are in box boxesRow,boxesCol
                    totalDistance += distanceFromGoal(boxesRow, boxesCol, boxes[boxesRow][boxesCol]);
                }
            }
        }

        return totalDistance;
    }

    /**
     * Returns the distance (row+col) the given coordinate is from a matching goal
     */
    private int distanceFromGoal(int i, int j, char box) {
        char boxGoal = Character.toLowerCase(box);
        return Node.goalLocations
                .stream()
                .filter(goalLocation -> {
                    return boxGoal == Node.goals[goalLocation.getKey()][goalLocation.getValue()];
                })
                .mapToInt((goalLocation) -> {
                    // we are at the correct type of goal
                    return Math.abs(goalLocation.getKey() - i) + Math.abs(goalLocation.getValue() - j);
                })
                .min()
                .getAsInt();
    }

    public abstract int f(Node n);
}
