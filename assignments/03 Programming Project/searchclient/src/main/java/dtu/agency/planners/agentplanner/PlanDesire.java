package dtu.agency.planners.agentplanner;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.planners.plans.PrimitivePlan;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PlanDesire { // everything the agent might want to achieve

    PriorityQueue<Pair<HLAction, PrimitivePlan>> desires;

    public PlanDesire() {
        desires = new PriorityQueue<>(new Comparator<Pair<HLAction, PrimitivePlan>>() {
            @Override
            public int compare(Pair<HLAction, PrimitivePlan> o1, Pair<HLAction, PrimitivePlan> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });
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
