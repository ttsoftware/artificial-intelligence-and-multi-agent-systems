package dtu;

import com.google.common.eventbus.EventBus;
import dtu.board.Level;
import dtu.events.EventBusService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PlannerClient {

    public static void main(String[] args) throws Exception {

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        // Create global event bus
        EventBus eventBus = EventBusService.getEventBus();

        Agency agency = new Agency(level);
        agency.run();
    }
}