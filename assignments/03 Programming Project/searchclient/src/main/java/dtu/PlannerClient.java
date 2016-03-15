package dtu;

import com.google.common.eventbus.EventBus;
import dtu.agent.AgentThread;
import dtu.events.Event;
import dtu.events.EventBusService;
import dtu.board.Level;
import dtu.events.EventSubscriber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlannerClient {

    public static void main(String[] args) throws Exception {

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        // Create global event bus
        EventBus eventBus = EventBusService.getEventBus();

        List<Thread> agentThreads = new ArrayList<>();

        level.getAgents().forEach(plan -> {
            // Start a new thread (agent) for each plan
            Thread t = new Thread(new AgentThread());
            agentThreads.add(t);
            t.start();
        });

        // Register events
        eventBus.register(new EventSubscriber());

        // Post events
        eventBus.post(new Event());

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