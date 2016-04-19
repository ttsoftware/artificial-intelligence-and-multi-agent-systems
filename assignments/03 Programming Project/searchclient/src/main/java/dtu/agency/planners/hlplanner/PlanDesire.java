package dtu.agency.planners.hlplanner;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.plans.PrimitivePlan;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;

class PlanDesire { // everything the agent might want to achieve

    private PriorityQueue<Pair<HLAction, PrimitivePlan>> desires;

    public PlanDesire() {
        desires = new PriorityQueue<>(
                (Comparator<Pair<HLAction, PrimitivePlan>>) (o1, o2) ->
                        o2.getValue().size() - o1.getValue().size());
    }

    public Pair<HLAction, PrimitivePlan> getBestDesire() {
        return desires.poll();
    }

    public void add(HLAction action, PrimitivePlan plan) {
        if (plan!=null) {
            desires.add( new Pair<>(action, plan) );
        }
    }

}
