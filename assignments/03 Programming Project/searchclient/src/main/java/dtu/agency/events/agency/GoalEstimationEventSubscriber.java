package dtu.agency.events.agency;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.EstimationEventSubscriber;
import dtu.agency.events.agent.GoalEstimationEvent;

import java.util.List;

public class GoalEstimationEventSubscriber extends EstimationEventSubscriber<GoalEstimationEvent> {

    public GoalEstimationEventSubscriber(Goal goal, List<Agent> agents) {
        super(goal, agents);
    }

    /**
     * Waits for all estimations to finish before returning. Will block calling thread.
     *
     * @return The agent whose estimation was lowest
     */
    public Agent getBestAgent() {
        try {
            // Wait for the value lowest estimation to be assigned
            estimationsThread.join();
            return agentEstimations.take().getAgent();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
