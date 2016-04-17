package dtu.agency.events.agency;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.AsyncEvent;
import dtu.agency.planners.ConcretePlan;

public class GoalAssignmentEvent extends AsyncEvent<ConcretePlan> {

    private final Agent agent;
    private final Goal goal;

    public GoalAssignmentEvent(Agent agent, Goal goal) {
        this.agent = agent;
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public Agent getAgent() {
        return agent;
    }
}