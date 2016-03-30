package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.actions.Action;
import dtu.agency.board.Level;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.SendServerActionsEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
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

                System.err.println("Recieved actions from Agency: " + event.getActions().size());
                sendActions(event.getActions());

                // Pretend problem is solved
                EventBusService.post(new ProblemSolvedEvent());
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
    private static void sendActions(Stack<Action> actions) {

        /*
        if (actions.size() != numberOfAgents) {
            throw new UnsupportedOperationException("Invalid number of actions. The number of actions must be equal to the number of agents.");
        }

        String serverAction = "";
        for (Action action : actions) {
            serverAction += action + ",";
        }

        // remove trailing ,
        serverAction = serverAction.substring(0, serverAction.length() -1);
        */

        while (!actions.empty()) {
            Action action = actions.pop();

            System.err.println("Trying: [" + action + "]");
            System.out.println("[" + action + "]");

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
                System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, action);
            }
        }
    }
}