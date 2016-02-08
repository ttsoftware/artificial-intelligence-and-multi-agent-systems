package dtu.searchclient.heuristic;

import dtu.searchclient.Node;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<Node> {

    public Node initialState;

    public Heuristic(Node initialState) {
        this.initialState = initialState;
    }

    public int compare(Node n1, Node n2) {
        return f(n1) - f(n2);
    }

    public int h(Node n) {
        // we wish to calculate the distance the boxes have from the goal
        char[][] boxes = n.getBoxes();

        int distanceFromGoal = 0;

        for (int boxesRow = 0; boxesRow < boxes.length; boxesRow++) {
            for (int boxesCol = 0; boxesCol < boxes[boxesRow].length; boxesCol++) {
                if (n.boxAt(boxesRow, boxesCol)) {
                    // we are in box boxesRow,boxesCol
                    distanceFromGoal += distanceFromGoal(boxesRow, boxesCol, boxes[boxesRow][boxesCol]);
                }
            }
        }

        return distanceFromGoal;
    }

    /**
     * Returns the distance (row+col) the given coordinate is from a matching goal
     */
    public int distanceFromGoal(int i, int j, char box) {
        char[][] goals = Node.getGoals();

        char boxGoal = Character.toLowerCase(box);
        int nearestGoal = -1;

        for (int goalRow = 0; goalRow < goals.length; goalRow++) {
            for (int goalCol = 0; goalCol < goals[goalRow].length; goalCol++) {
                if (Node.goalAt(goalRow, goalCol)) {
                    // we are at goal goalRow,goalCol
                    if (boxGoal == goals[goalRow][goalCol]) {
                        // we are at the correct type of goal
                        int distance = Math.abs(goalRow - i) + Math.abs(goalCol - j);
                        if (distance < nearestGoal || nearestGoal == -1) {
                            nearestGoal = distance;
                        }
                    }
                }
            }
        }

        return nearestGoal;
    }

    public abstract int f(Node n);
}
