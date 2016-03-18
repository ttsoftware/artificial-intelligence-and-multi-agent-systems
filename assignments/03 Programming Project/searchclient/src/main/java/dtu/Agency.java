package dtu;

import dtu.agent.AgentThread;
import dtu.board.Level;
import dtu.events.EventBusService;
import dtu.events.agent.GoalOfferEvent;

import java.util.ArrayList;
import java.util.List;

public class Agency implements Runnable {

    private Level level;

    public Agency(Level level) {
        this.level = level;
    }

    @Override
    public void run() {

        List<Thread> agentThreads = new ArrayList<>();

        level.getAgents().forEach(agent -> {
            // Start a new thread (agent) for each plan
            Thread t = new Thread(new AgentThread(agent));
            agentThreads.add(t);
            t.start();
        });

        // Register events
        // eventBus.register(new GoalOfferEventSubscriber());

        // Post events to
        level.getGoalQueue().forEach(goal -> {
            EventBusService.getEventBus().post(new GoalOfferEvent(goal));
        });

        // wait for all threads to finish
        /*agentThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });*/
    }
}
