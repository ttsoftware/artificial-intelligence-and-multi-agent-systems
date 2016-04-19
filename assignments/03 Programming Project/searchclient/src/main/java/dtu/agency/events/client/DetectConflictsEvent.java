package dtu.agency.events.client;

import dtu.agency.events.AsyncEvent;
import dtu.agency.planners.plans.ConcretePlan;

import java.util.HashMap;

public class DetectConflictsEvent extends AsyncEvent<Boolean> {

    final HashMap<Integer, ConcretePlan> currentPlans;

    public DetectConflictsEvent(HashMap<Integer, ConcretePlan> currentPlans) {
        this.currentPlans = currentPlans;
    }

    public HashMap<Integer, ConcretePlan> getCurrentPlans() {
        return currentPlans;
    }
}
