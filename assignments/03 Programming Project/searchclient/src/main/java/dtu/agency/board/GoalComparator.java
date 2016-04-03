package dtu.agency.board;

import java.util.Comparator;

public class GoalComparator implements Comparator<Goal> {

    @Override
    public int compare(Goal goal1, Goal goal2) {
        return goal1.getWeight() - goal2.getWeight();
    }
}
