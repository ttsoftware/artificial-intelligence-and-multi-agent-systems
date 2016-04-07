package dtu.agency.agent.bdi;

import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.htn.HTNState;

/**
 * Created by koeus on 4/7/16.
 */
public class AgentPlan {
    PrimitivePlan plan;       // List of primitive actions
    Position agentBeforePlan; // the expected accumulated state after plan is realised
    Position agentAfterPlan;
    String targetBox;         // targeted at some box
    Position targetBoxBeforePlan;
    Position targetBoxAfterPlan;

    AgentPlan(PrimitivePlan plan, HTNState initialState, Box targetBox) {
        this.plan = plan;
        agentBeforePlan = initialState.getAgentPosition();
    }

    public PrimitivePlan getPlan() {
        return plan;
    }

    public String getTargetBox() {
        return targetBox;
    }

    public Position getAgentAfterPlan() {
        return agentAfterPlan;
    }
}


