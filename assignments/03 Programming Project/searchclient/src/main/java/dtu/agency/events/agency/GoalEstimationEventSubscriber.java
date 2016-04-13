package dtu.agency.events.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agent.GoalEstimationEvent;

import java.util.concurrent.PriorityBlockingQueue;

public class GoalEstimationEventSubscriber implements EventSubscriber<GoalEstimationEvent> {

    private GoalEstimationEvent lowestEstimation;

    private final Goal goal;
    private final int numberOfAgents;

    private final Thread estimationsThread;

    private PriorityBlockingQueue<GoalEstimationEvent> agentEstimations = new PriorityBlockingQueue<>();

    public GoalEstimationEventSubscriber(Goal goal, int numberOfAgents) {
        this.goal = goal;
        this.numberOfAgents = numberOfAgents;

        // Initialize thread for synchronizing steps
        estimationsThread = new Thread(() -> {
            try {
                synchronized (goal) {
                    // Wait for all agents to finish
                    while (agentEstimations.size() != numberOfAgents) {
                        System.err.println(
                                "Waiting for " + (numberOfAgents - agentEstimations.size()) + " estimations"
                        );
                        goal.wait();
                    }
                    lowestEstimation = agentEstimations.take();
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });
        estimationsThread.start();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void changeSubscriber(GoalEstimationEvent event) {
        agentEstimations.offer(event);
        // notify estimationsThread to see if all agents have estimated
        synchronized (goal) {
            goal.notify();
        }
    }

    public Goal getGoal() {
        return goal;
    }

    /**
     * Waits for all estimations to finish before returning. Will block calling thread.
     *
     * @return The label of the agent whose estimation was lowest
     */
    public String getBestAgent() {
        try {
            // Wait for the value lowest estimation to be assigned
            estimationsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return lowestEstimation.getAgentLabel();
    }
}
