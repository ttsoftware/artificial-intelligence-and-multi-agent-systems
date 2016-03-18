package dtu.agent;

import dtu.board.Agent;
import dtu.board.Goal;
import dtu.events.EventBusService;
import dtu.events.agent.GoalOfferEvent;
import dtu.events.agency.GoalEstimationEventSubscriber;
import dtu.events.agent.StopAllAgentsEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgentThreadTest {

    @Test
    public void testRun() throws InterruptedException {

        Agent agent1 = new Agent("0");
        Agent agent2 = new Agent("1");

        Thread t1 = new Thread(new AgentThread(agent1));
        Thread t2 = new Thread(new AgentThread(agent2));
        t1.start();
        t2.start();

        Goal goal = new Goal("A", 0, 0, 0);

        List<String> agentLabels = new ArrayList<>();
        agentLabels.add(agent1.getLabel());
        agentLabels.add(agent2.getLabel());

        GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(agentLabels);
        EventBusService.getEventBus().register(goalEstimationEventSubscriber);

        EventBusService.getEventBus().post(new GoalOfferEvent(goal));

        HashMap<String, Integer> agentStepsEstimation = goalEstimationEventSubscriber.getAgentStepsEstimation();
        agentStepsEstimation.keySet().forEach(agentLabel -> {
            System.out.println("Agency recieved estimation for agent " + agentLabel + ": " + Integer.toString(agentStepsEstimation.get(agentLabel)));
        });

        // we are done
        EventBusService.getEventBus().post(new StopAllAgentsEvent());

        t1.join();
        t2.join();
    }
}
