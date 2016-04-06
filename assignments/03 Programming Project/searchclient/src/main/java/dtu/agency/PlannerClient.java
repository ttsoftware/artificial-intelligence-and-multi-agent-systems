package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.board.Level;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.SendServerActionsEvent;
import dtu.agency.services.EventBusService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class PlannerClient {

    private static BufferedReader serverMessages;
    private static int numberOfAgents;

    public static void main(String[] args) throws Exception {

        serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        numberOfAgents = level.getAgents().size();

        // Register for actions event
        EventBusService.register(new EventSubscriber<SendServerActionsEvent>() {

            @Subscribe
            @AllowConcurrentEvents
            public void changeSubscriber(SendServerActionsEvent event) {

                System.err.println("Received actions from Agency: " + event.getConcreteActions().size());
                sendActions(event.getConcreteActions());

                // Pretend problem is solved
                // EventBusService.post(new ProblemSolvedEvent());
            }
        });

        Agency agency = new Agency(level);
        agency.run();
    }

    /**
     * Interact with the server
     *
     * @param concreteActions One or more concreteActions to send to the server
     */
    private static void sendActions(Stack<ConcreteAction> concreteActions) {

        /*
        if (concreteActions.size() != numberOfAgents) {
            throw new UnsupportedOperationException("Invalid number of concreteActions. The number of concreteActions must be equal to the number of agents.");
        }

        String serverAction = "";
        for (ConcreteAction action : concreteActions) {
            serverAction += action + ",";
        }

        // remove trailing ,
        serverAction = serverAction.substring(0, serverAction.length() -1);
        */

        while (!concreteActions.empty()) {
            ConcreteAction concreteAction = concreteActions.pop();

            System.err.println("Trying: [" + concreteAction + "]");
            System.out.println("[" + concreteAction + "]");

            String response = null;
            try {
                response = serverMessages.readLine();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            if (response == null) {
                System.err.format("Lost contact with the server. We stop now");
                System.exit(1);
            }
            if (response.contains("false")) {
                System.err.format("Server responded with %s to the inapplicable concreteAction: %s\n", response, concreteAction);
            }
        }
    }
}