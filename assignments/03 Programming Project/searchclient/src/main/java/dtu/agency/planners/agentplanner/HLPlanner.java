package dtu.agency.planners.agentplanner;

import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.PrimitivePlan;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.PlanningLevelService;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide
 * high level tasks all the way into sequences of primitive actions
 */
public class HLPlanner {

    private PlanningLevelService pls; // location of agent and boxes
    private PlanDesire desires;               // what the agent would desire to do next in the planning phase
    PlanIntention intentions;         // the hierarchy of intentions build from the original SolveGoalAction.
    HTNPlanner htnPlanner;

    public HLPlanner( HTNPlanner htnPlanner ) {
        pls = new PlanningLevelService(htnPlanner.getIntention().getBox());
        this.htnPlanner = htnPlanner;
        intentions = new PlanIntention( htnPlanner.getIntention() );
        intentions.setCurrentIntention( htnPlanner.getIntention() );
        desires = new PlanDesire();
    }

    public PrimitivePlan llPlan() {
        // return a low level plan of concrete actions, if one exist
        htnPlanner.setRelaxationMode(RelaxationMode.None);
        return htnPlanner.plan();
    }

    public HLPlan plan() {
        // change this to return an ordered list of HLActions, which if  performed in this order
        // will solve the problem in topLevelIntention
        return new HLPlan( intentions.getTopLevelIntention() );
    }

}
