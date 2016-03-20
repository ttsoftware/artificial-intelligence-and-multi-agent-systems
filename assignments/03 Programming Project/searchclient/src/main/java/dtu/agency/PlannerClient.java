package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import dtu.agency.agent.actions.Action;
import dtu.agency.board.Level;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.SendActionsEvent;
import dtu.agency.events.agent.StopAllAgentsEvent;
import dtu.agency.services.EventBusService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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

        // Create global event bus
        EventBus eventBus = EventBusService.getEventBus();

        // Register for actions event
        eventBus.register(new EventSubscriber<SendActionsEvent>() {

            @Override
            @AllowConcurrentEvents
            public void changeSubscriber(SendActionsEvent event) {
                sendActions(event.getActions());
            }
        });

        Agency agency = new Agency(level);
        agency.run();
    }

    /**
     * Interact with the server
     *
     * @param actions One or more actions to send to the server
     */
    private static void sendActions(List<Action> actions) {

        if (actions.size() != numberOfAgents) {
            throw new UnsupportedOperationException("Invalid number of actions. The number of actions must be equal to the number of agents.");
        }

        String serverAction = "";
        for (Action action : actions) {
            serverAction += action + ",";
        }
        // remove trailing ,
        serverAction = serverAction.substring(0, -1);

        System.err.println("Trying: [" + serverAction + "]");
        System.out.println("[" + serverAction + "]");
        String response = null;
        try {
            response = serverMessages.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            System.err.format("Lost contact with the server. We stop now");
            EventBusService.getEventBus().post(new StopAllAgentsEvent());
            System.exit(1);
        }
        if (response.contains("false")) {
            System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, serverAction);
        }
    }
}