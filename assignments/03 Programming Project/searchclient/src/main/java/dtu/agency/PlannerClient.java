package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
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

        Thread t = new Thread(PlannerClient::sendActions);
        t.start();

        Agency agency = new Agency(level);
        agency.run();

        // Join when problem has been solved
        t.join();
    }

    /**
     * Interact with the server. Pop the next stack of actions.
     */
    public static void sendActions() {

        HashMap<Integer, ConcretePlan> currentPlans = new HashMap<>();
        SendServerActionsEvent sendActionsEvent = null;

        try {
            // .take() will call Thread.wait() until an element (Stack) becomes available
            sendActionsEvent = sendServerActionsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        // We take the next collection of plans from the queue
        while (sendActionsEvent != null) {
            currentPlans.put(
                    Integer.valueOf(sendActionsEvent.getAgent().getLabel()),
                    sendActionsEvent.getConcretePlan()
            );
            // poll next element, without waiting
            sendActionsEvent = sendServerActionsQueue.poll();
        }

        // we should now have emptied the queue
        assert sendServerActionsQueue.size() == 0;

        int firstPlanActionCount = currentPlans.get(0).getActions().size();

        HashMap<Integer, ConcreteAction> agentsActions = new HashMap<>();
        // pop the next action from each plan
        currentPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.getActions().pop()));

        // send actions to server
        send(buildActionSet(agentsActions));

        // we should be missing an action in the plan
        assert currentPlans.get(0).getActions().size() == firstPlanActionCount - 1;

        // add plans back into the stack - they are now missing an action each
        currentPlans.forEach((agentNumber, concretePlan) -> {
            if (concretePlan.getActions().size() != 0) {
                // Add plan if it has at least 1 move left
                sendServerActionsQueue.add(new SendServerActionsEvent(
                        new Agent(Integer.toString(agentNumber)),
                        concretePlan
                ));
            }
            else {
                // TODO: Somehow notify Agency that an agent has finished it's plan
            }
        });

        // TODO At some point we should stop this recursion. How do we know that the whole level is solved?

        // Send the next set of actions
        sendActions();
    }

    public static void send(String toServer) {
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
    }

    public static String buildActionSet(HashMap<Integer, ConcreteAction> agentsPlans) {
        return buildActionSet(agentsPlans, numberOfAgents);
    }

    public static String buildActionSet(HashMap<Integer, ConcreteAction> agentsActions, int numberOfAgents) {
        // take one action from each plan and combine them
        StringJoiner toServerBuilder = new StringJoiner(",", "[", "]");

        // Java 8 is awesome
        IntStream.range(0, numberOfAgents).forEach(i -> {
            ConcreteAction agentAction = agentsActions.get(i);
            if (agentAction != null) {
                // append the action
                toServerBuilder.add(agentAction.toString());
            } else {
                // we must add a NoOp for this agent at this time
                toServerBuilder.add(new NoConcreteAction().toString());
            }
        });

        return toServerBuilder.toString();
    }
}