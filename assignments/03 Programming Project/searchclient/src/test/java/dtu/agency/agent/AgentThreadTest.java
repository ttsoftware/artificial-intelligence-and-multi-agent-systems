package dtu.agency.agent;

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.services.EventBusService;
import org.junit.Test;

public class AgentThreadTest {

    @Test
    public void testRun() throws InterruptedException {

        Agent agent1 = new Agent("0", new Position(1,2));
        Agent agent2 = new Agent("1", new Position(2,2));

        Thread t1 = new Thread(new AgentThread(agent1));
        Thread t2 = new Thread(new AgentThread(agent2));
        t1.start();
        t2.start();

        Goal goal = new Goal("A", new Position(0, 0), 0);

        GoalEstimationEventSubscriber goalEstimationEventSubscriber = new GoalEstimationEventSubscriber(goal, 2);
        EventBusService.register(goalEstimationEventSubscriber);

        EventBusService.post(new GoalOfferEvent(goal));

        // System.out.println("Agency recieved estimation for agency " + estimationEvent.getAgentLabel() + ": " + Integer.toString(estimationEvent.getSteps()));

        t1.join();
        t2.join();
    }
}
