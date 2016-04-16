package dtu.agency.actions.abstractaction.actioncomparators;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

import java.util.Comparator;

/**
 * Comparator to compare HLActions
 */
public class HLActionComparator implements Comparator<HLAction> {

    private final PlanningLevelService pls;

    public HLActionComparator(PlanningLevelService pls){
        this.pls = pls;
    }

    @Override
    public int compare(HLAction o1, HLAction o2) {

        return o2.approximateSteps(pls) - o1.approximateSteps(pls);
    }
}
