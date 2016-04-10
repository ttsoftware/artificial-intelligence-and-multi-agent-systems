package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.SendServerActionsEvent;
import dtu.agency.planners.ConcretePlan;
import dtu.agency.services.EventBusService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class PlannerClient {

    private static BufferedReader serverMessages;
    private static int numberOfAgents;
    private static ArrayBlockingQueue<SendServerActionsEvent> sendServerActionsQueue;

    public static void main(String[] args) throws Exception {

        serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        numberOfAgents = level.getAgents().size();
        sendServerActionsQueue = new ArrayBlockingQueue<>(numberOfAgents);

        // Register for actions event
        EventBusService.register(new EventSubscriber<SendServerActionsEvent>() {

            @Subscribe
            @AllowConcurrentEvents
            public void changeSubscriber(SendServerActionsEvent event) {

                System.err.println("Received a plan from Agency with " + event.getConcretePlan().getActions().size() + " actions.");

                try {
                    sendServerActionsQueue.add(event);
                } catch (IllegalStateException e) {
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

        HashMap<Integer, ConcretePlan> currentPlans = new HashMap<>();

        try {
            SendServerActionsEvent sendActionsEvent;

            // We take the next collection of plans from the queue
            // .take() will call Thread.wait() until an element (Stack) becomes available
            while ((sendActionsEvent = sendServerActionsQueue.take()) != null) {
                currentPlans.put(
                        Integer.valueOf(sendActionsEvent.getAgent().getLabel()),
                        sendActionsEvent.getConcretePlan()
                );
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        // we should now have emptied the queue
        assert sendServerActionsQueue.size() == 0;

        // take one action from each plan and combine them
        StringJoiner toServerBuilder = new StringJoiner(",", "[", "]");

        // Java 8 is awesome
        IntStream.range(0, numberOfAgents - 1).forEach(i -> {
            ConcretePlan agentPlan = currentPlans.get(i);
            if (agentPlan != null) {
                // append the action
                toServerBuilder.add(agentPlan.getActions().pop().toString());
                // update the plan map with the now -1 action plan
                currentPlans.put(i, agentPlan);
            } else {
                // we must add a NoOp for this agent at this time
                toServerBuilder.add(new NoConcreteAction().toString());
            }
        });

        String toServer = toServerBuilder.toString();

        // TODO: send the combined actions to server
        System.err.println("Trying: " + toServer);
        System.out.println(toServer);

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
            System.err.format("Server responded with %s to: %s\n", response, toServer);
        }

        // add plans back into the stack - they are now missing an action each
        currentPlans.forEach((agentNumber, concretePlan) -> {
            sendServerActionsQueue.add(new SendServerActionsEvent(
                    new Agent(Integer.toString(agentNumber)),
                    concretePlan
            ));
        });

        // TODO At some point we should stop this recursion

        // Send the next set of actions
        sendActions();
    }
}