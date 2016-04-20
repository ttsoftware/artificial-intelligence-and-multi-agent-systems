package dtu.agency.planners.plans.comparators;

import dtu.agency.planners.plans.PrimitivePlan;

import java.util.Comparator;

/**
 * Comparator to compare Primitive Plans
 */
public class PrimitivePlanComparator implements Comparator<PrimitivePlan> {
    @Override
    public int compare(PrimitivePlan o1, PrimitivePlan o2) {

        return o2.size() - o1.size();
    }
}
