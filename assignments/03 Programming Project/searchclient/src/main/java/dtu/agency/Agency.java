package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Level;
import dtu.agency.events.agency.StopAllAgentsEvent;
import dtu.agency.events.agent.GoalEstimationEventSubscriber;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.services.EventBusService;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;

public class Agency implements Runnable {

    private List<Thread> agentThreads;

    public Agency(Level level) {
        LevelService.getInstance().setLevel(level);
    }

    @Override
    public void run() {

        List<String> agentLabels = new ArrayList<>();
        agentThreads = new ArrayList<>();

        LevelService.getInstance().getLevel().getAgents().forEach(agent -> {
            // Start a new thread (agency) for each plan
            Thread t = new Thread(new AgentThread(agent));
            agentThreads.add(t);
            agentLabels.add(agent.getLabel());
            t.start();
        });

        // Register for incoming plans
        EventBusService.getEventBus().register(this);

        // Register for incoming goal estimations
        GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(agentLabels);
        EventBusService.getEventBus().register(goalEstimationEventSubscriber);

        // Offer goals to agents
        LevelService.getInstance().getLevel().getGoalQueue().forEach(goal -> {
            EventBusService.getEventBus().post(new GoalOfferEvent(goal));
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Subscribe
    public void problemSolverdEventSubscriber(ProblemSolvedEvent event) {
        // wait for all threads to finish
        EventBusService.getEventBus().post(new StopAllAgentsEvent());
        agentThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}