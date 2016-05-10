package dtu.agency.agent;

import dtu.agency.ProblemMarshallerTest;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgentThreadTest {
    private static Level twoAgents;

    @BeforeClass
    public static void setUp() throws IOException {
        twoAgents = ProblemMarshallerTest.marshall("/two_agents.lvl");
    }

    @Test
    public void testRun() throws InterruptedException {
        GlobalLevelService.getInstance().setLevel(twoAgents);

        Agent agent1 = new Agent("0");
        Agent agent2 = new Agent("1");

        List<Agent> agents = new ArrayList<>();
        agents.add(agent1);
        agents.add(agent2);

        Thread t1 = new Thread(new AgentThread());
        Thread t2 = new Thread(new AgentThread());
        t1.start();
        t2.start();

        Goal goal = new Goal("A", new Position(0, 0), 0);

        GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(goal, agents);
        EventBusService.register(goalEstimationEventSubscriber);

        EventBusService.post(new GoalOfferEvent(goal));
        EventBusService.post(new GoalOfferEvent(goal));

        // System.out.println("Agency recieved estimation for agency " + estimationEvent.getAgentLabel() + ": " + Integer.toString(estimationEvent.getSteps()));

        t1.join();
        t2.join();
    }
}
