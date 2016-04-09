package dtu.agency.planners.agentplanner;


import dtu.agency.agent.bdi.AgentBelief;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.PrimitivePlan;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class AgentPlanner  {

    AgentBelief beliefs;      // location of agent and boxes
    PlanDesire desires;       // what the agent would desire to do next in the planning phase
    PlanIntention intentions; // the hierarchy of intentions build from the original SolveGoalAction.
    HTNPlanner htnPlanner;

    public AgentPlanner(AgentBelief belief, HTNPlanner htnPlanner ) {
        beliefs = belief;
        this.htnPlanner = htnPlanner;
        intentions = new PlanIntention( htnPlanner.getIntention() );
        intentions.setCurrentIntention( htnPlanner.getIntention() );
        desires = new PlanDesire();
    }

    public PrimitivePlan plan() {
        return htnPlanner.plan();
    }


/*
    // check that the plan in its entirety is 'sound', that is that all cells in the path is free
    if (!planIsSound(plan, intention)) {
        ; // replan! or extend plan until it is
        // or send distress communication and ask for help/forgiveness/permission to commit suicide/whatever
    }
*/

}
