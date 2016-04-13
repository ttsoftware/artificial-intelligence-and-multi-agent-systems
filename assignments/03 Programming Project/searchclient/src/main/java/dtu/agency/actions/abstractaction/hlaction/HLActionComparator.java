package dtu.agency.actions.abstractaction.hlaction;

import dtu.agency.board.Position;

import java.util.Comparator;

/**
 * Created by koeus on 4/13/16.
 */
public class HLActionComparator implements Comparator<HLAction> {

    Position agentOrigin;

    public HLActionComparator(Position agentOrigin){
        this.agentOrigin = agentOrigin;
    }

    @Override
    public int compare(HLAction o1, HLAction o2) {

        return o2.approximateSteps(agentOrigin) - o1.approximateSteps(agentOrigin);
    }
}
