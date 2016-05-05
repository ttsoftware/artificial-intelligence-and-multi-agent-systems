package dtu.agency.events.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.BoardObject;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agent.MoveObstacleEstimationEvent;

import java.util.concurrent.PriorityBlockingQueue;

public class MoveObstacleEstimationEventSubscriber implements EventSubscriber<MoveObstacleEstimationEvent> {

    private final BoardObject obstacle;
    private final int numberOfAgents;

    private final Thread obstacleEstimationsThread;

    private PriorityBlockingQueue<MoveObstacleEstimationEvent> agentEstimations = new PriorityBlockingQueue<>();

    public MoveObstacleEstimationEventSubscriber(BoardObject obstacle, int numberOfAgents) {
        this.obstacle = obstacle;
        this.numberOfAgents = numberOfAgents;

        // Initialize thread for synchronizing steps
        obstacleEstimationsThread = new Thread(() -> {
            try {
                synchronized (obstacle) {
                    // Wait for all agents to finish
                    while (agentEstimations.size() != numberOfAgents) {
                        obstacle.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });
        obstacleEstimationsThread.start();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void changeSubscriber(MoveObstacleEstimationEvent event) {
        if (event.getObstacle().equals(obstacle)) {
            // The estimation is for this given obstacle
            agentEstimations.offer(event);
            // notify obstacleEstimationsThread to see if all agents have estimated
            synchronized (obstacle) {
                obstacle.notify();
            }
        }
    }

    public BoardObject getObstacle() {
        return obstacle;
    }

    /**
     * Waits for all estimations to finish before returning. Will block calling thread.
     *
     * @return The agent whose estimation was lowest
     */
    public PriorityBlockingQueue<MoveObstacleEstimationEvent> getEstimations() {
        try {
            // Wait for the value lowest estimation to be assigned
            obstacleEstimationsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return agentEstimations;
    }
}
