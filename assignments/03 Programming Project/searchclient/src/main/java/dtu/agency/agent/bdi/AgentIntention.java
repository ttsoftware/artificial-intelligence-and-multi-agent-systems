package dtu.agency.agent.bdi;


import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.NoAction;
import dtu.agency.board.Agent;
import dtu.agency.services.GlobalLevelService;

import java.util.LinkedList;

public class AgentIntention {
    // Intentions are really High Level SolveGoalActions()
    LinkedList<HLAction> topLevelIntentions; // orders from the agency (or other agents)
    HLAction currentIntention;

    public AgentIntention(Agent agent) {
        topLevelIntentions = new LinkedList<>();
        currentIntention = new NoAction(GlobalLevelService.getInstance().getPosition(agent));
    }

    public HLAction viewCurrentTopLevelIntention(){
        return topLevelIntentions.peekFirst();
    }

    public HLAction removeCurrentTopLevelIntention() {
        return topLevelIntentions.pollFirst();
    }

    public void addTopLevelIntention(HLAction intention) {
        this.topLevelIntentions.add(intention);
    }

    public void addPrioritizedTopLevelIntention(HLAction intention) {
        this.topLevelIntentions.addFirst(intention);
    }

    public HLAction getCurrentIntention() {
        return currentIntention;
    }

    public void setCurrentIntention(HLAction currentIntention) {
        this.currentIntention = currentIntention;
    }
}
