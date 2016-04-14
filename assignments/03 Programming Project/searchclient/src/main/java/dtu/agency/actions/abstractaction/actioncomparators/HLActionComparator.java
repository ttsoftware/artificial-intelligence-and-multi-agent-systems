package dtu.agency.actions.abstractaction.actioncomparators;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.board.Position;

import java.util.Comparator;

/**
 * Comparator to compare HLActions
 */
public class HLActionComparator implements Comparator<HLAction> {

    private final Position agentOrigin;

    public HLActionComparator(Position agentOrigin){
        this.agentOrigin = agentOrigin;
    }

    @Override
    public int compare(HLAction o1, HLAction o2) {

        return o2.approximateSteps(agentOrigin) - o1.approximateSteps(agentOrigin);
    }
}
