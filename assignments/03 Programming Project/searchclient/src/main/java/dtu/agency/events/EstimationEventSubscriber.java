package dtu.agency.events;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class EstimationEventSubscriber<T extends EstimationEvent> implements EventSubscriber<T> {

    protected final Object synchronizer;

    private final BoardObject task;
    private final List<Agent> agents;
    protected PriorityBlockingQueue<T> agentEstimations = new PriorityBlockingQueue<>();

    protected final Thread estimationsThread;

    public EstimationEventSubscriber(BoardObject task, List<Agent> agents) {
        this.task = task;
        this.agents = agents;
        this.synchronizer = new Object();

        // Initialize thread for synchronizing steps
        estimationsThread = new Thread(() -> {
            try {
                synchronized (synchronizer) {
                    // Wait for all agents to finish
                    while (!isFinished()) {
                        synchronizer.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });
        estimationsThread.start();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void changeSubscriber(T event) {
        if (event.getTask().equals(task)) {
            // The estimation is for this given obstacle
            agentEstimations.offer(event);
            // notify obstacleEstimationsThread to see if all agents have estimated
            synchronized (synchronizer) {
                synchronizer.notify();
            }
        }
    }

    /**
     * Have all agents finished estimating?
     * @return
     */
    private boolean isFinished() {
        boolean isFinished = true;
        for (Agent agent : agents) {
            boolean isAgentFinished = false;
            for (T estimation : agentEstimations) {
                if (estimation.getAgent().equals(agent)) {
                    isAgentFinished = true;
                    break;
                }
            }
            isFinished &= isAgentFinished;
            if (!isFinished) break;
        }
        return isFinished;
    }

    /**
     * Waits for all estimations to finish before returning. Will block calling thread.
     *
     * @return The agent whose estimation was lowest
     */
    public PriorityBlockingQueue<T> getEstimations() {
        try {
            // Wait for the value lowest estimation to be assigned
            estimationsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return agentEstimations;
    }
}
