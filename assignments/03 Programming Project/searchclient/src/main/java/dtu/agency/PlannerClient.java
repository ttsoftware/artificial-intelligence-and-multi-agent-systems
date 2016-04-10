package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Level;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.SendServerActionsEvent;
import dtu.agency.planners.ConcretePlan;
import dtu.agency.services.EventBusService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class PlannerClient {

    private static BufferedReader serverMessages;
    private static int numberOfAgents;
    private static ArrayBlockingQueue<ConcretePlan> agentActionsQueue;

    public static void main(String[] args) throws Exception {

        serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        numberOfAgents = level.getAgents().size();
        agentActionsQueue = new ArrayBlockingQueue<>(numberOfAgents);

        // Register for actions event
        EventBusService.register(new EventSubscriber<SendServerActionsEvent>() {

            @Subscribe
            @AllowConcurrentEvents
            public void changeSubscriber(SendServerActionsEvent event) {

                System.err.println("Received a plan from Agency with " + event.getConcretePlan().getActions().size() + " actions.");

                try {
                    agentActionsQueue.add(event.getConcretePlan());
                }
                catch (IllegalStateException e) {
                    // We are trying to add more Stacks than agents
                    e.printStackTrace(System.err);
                }

                // Pretend problem is solved
                // EventBusService.post(new ProblemSolvedEvent());
            }
        });

        Agency agency = new Agency(level);
        agency.run();

        Thread t = new Thread(PlannerClient::sendActions);
        t.start();

        // Join when problem has been solved
        // t.join();
    }

    /**
     * Interact with the server. Pop the next stack of actions.
     */
    private static void sendActions() {

        ConcretePlan agentActions;

        List<ConcretePlan> tempPlans = new ArrayList<>();

        try {
            // We take the next collection of actions from the queue
            // .take() will call Thread.wait() until a stack becomes available
            while ((agentActions = agentActionsQueue.take()) != null) {

                tempPlans.add(agentActions);
            }

            // take one action from each plan
            for (ConcretePlan plan : tempPlans) {

                // TODO: send the combined actions to server
            }

            // add plans back into the stack
            agentActionsQueue.addAll(tempPlans);

            // send all the actions in this stack
            /*while (!agentActions.empty()) {
                ConcreteAction concreteAction = agentActions.pop();

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
            }*/
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}