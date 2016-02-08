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

        /*int totalDistance = distanceFromGoal(
                boxLocation.getKey(),
                boxLocation.getValue(),
                boxes[boxLocation.getKey()][boxLocation.getValue()]
        );*/

        int totalDistance = 0;

        for (Pair<Integer, Integer> boxLocation : n.getBoxesLocations()) {
            totalDistance += distanceFromGoal(
                    boxLocation.getKey(),
                    boxLocation.getValue(),
                    boxes[boxLocation.getKey()][boxLocation.getValue()]
            );
        }

        int oldTotalDistance = 0;

        for (int boxesRow = 0; boxesRow < boxes.length; boxesRow++) {
            for (int boxesCol = 0; boxesCol < boxes[boxesRow].length; boxesCol++) {
                if (n.boxAt(boxesRow, boxesCol)) {
                    // we are in box boxesRow,boxesCol
                    oldTotalDistance += distanceFromGoal(boxesRow, boxesCol, boxes[boxesRow][boxesCol]);
                }
            }
        }

        return oldTotalDistance;
    }

    /**
     * Returns the distance (row+col) the given coordinate is from a matching goal
     */
    private int distanceFromGoal(int i, int j, char box) {
        char boxGoal = Character.toLowerCase(box);

        final int[] nearestGoalDistance = {-1};

        Node.goalLocations
                .parallelStream()
                .forEach((goalLocation) -> {
                    if (boxGoal == Node.goals[goalLocation.getKey()][goalLocation.getValue()]) {
                        // we are at the correct type of goal
                        int distance = Math.abs(goalLocation.getKey() - i) + Math.abs(goalLocation.getValue() - j);
                        if (distance < nearestGoalDistance[0] || nearestGoalDistance[0] == -1) {
                            nearestGoalDistance[0] = distance;
                        }
                    }
                });

        return nearestGoalDistance[0];
    }

    public abstract int f(Node n);
}
