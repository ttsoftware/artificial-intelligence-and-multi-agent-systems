package dtu.agency.events.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.Event;
import dtu.agency.planners.plans.ConcretePlan;

public class PlanOfferEvent extends Event {

    private final Goal goal;
    private final Agent agent;
    private final ConcretePlan plan;

    /**
     *
     * @param goal
     * @param agent
     * @param plan
     */
    public PlanOfferEvent(Goal goal, Agent agent, ConcretePlan plan) {
        this.goal = goal;
        this.agent = agent;
        this.plan = plan;
    }

    public Goal getGoal() {
        return goal;
    }

    public Agent getAgent() {
        return agent;
    }

    public ConcretePlan getPlan() {
        return plan;
    }
}
