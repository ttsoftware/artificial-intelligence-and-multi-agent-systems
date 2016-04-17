package dtu.agency.planners.hlplanner;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.services.BDIService;

/**
 * High Level Effect, to be applied on a PlanningLevelService, when planning ahead in time
 */
public class HLEffect {

    public final Position agentOrigin;
    public final Position agentDestination;
    public final Box box;
    public final Position boxOrigin;
    public final Position boxDestination;

    public HLEffect(HLEffect other){
        agentOrigin = other.agentOrigin;
        agentDestination = other.agentDestination;
        box = other.box;
        boxOrigin = other.boxOrigin;
        boxDestination = other.boxDestination;
    }

    public HLEffect(Position agentFrom, Position agentTo){
        agentOrigin = agentFrom;
        agentDestination = agentTo;
        box = null;
        boxOrigin = null;
        boxDestination = null;
    }

    public HLEffect(Position agentFrom, Position agentTo, Box targetBox, Position boxFrom, Position boxTo){
        agentOrigin = agentFrom;
        agentDestination = agentTo;
        box = targetBox;
        boxOrigin = boxFrom;
        boxDestination = boxTo;
    }

    @Override
    public String toString(){
        Agent agent = BDIService.getInstance().getAgent();
        String s = "HLEffect: Ag:[" + agent + "]:" + agentOrigin + "->" + agentDestination;
        if (box != null) {
            s += " Bx[" + box + "]:" +boxOrigin + "->" +boxDestination;
        }
        return s;
    }
}
