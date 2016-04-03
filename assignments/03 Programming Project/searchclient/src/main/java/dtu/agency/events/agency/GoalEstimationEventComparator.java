package dtu.agency.events.agency;

import dtu.agency.events.agent.GoalEstimationEvent;

import java.util.Comparator;

public class GoalEstimationEventComparator implements Comparator<GoalEstimationEvent> {

    @Override
    public int compare(GoalEstimationEvent eventA, GoalEstimationEvent eventB) {
        return eventA.getSteps() - eventB.getSteps();
    }
}
