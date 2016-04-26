package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import dtu.agency.conflicts.Conflict;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.pop.GotoPOP;
import dtu.agency.services.ConflictService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
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

        // Create the level service
        GlobalLevelService.getInstance().setLevel(level);

        // Prioritize goals
        GotoPOP gotoPlanner = new GotoPOP();
        GlobalLevelService.getInstance().updatePriorityQueues(gotoPlanner.getWeighedGoals());

        numberOfAgents = level.getAgents().size();
        sendServerActionsQueue = new ArrayBlockingQueue<>(numberOfAgents);

        ThreadService.setNumberOfAgents(numberOfAgents);

        // Thread which actually communicates with the server
        Thread sendActionsThread = new Thread(PlannerClient::sendActions);
        sendActionsThread.start();

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
            }
        });

        EventBusService.register(new EventSubscriber<ProblemSolvedEvent>() {

            @Override
            public void changeSubscriber(ProblemSolvedEvent event) {

                System.err.println("We solved the entire goal!");

                // Join when problem has been solved
                try {
                    sendActionsThread.interrupt();
                    sendActionsThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });

        Thread agencyThread = new Thread(new Agency());
        agencyThread.start();
        agencyThread.join();

        System.err.println("Agency was joined.");
        // new Agency(level).run();
    }

    /**
     * Interact with the server. Pop the next stack of actions.
     */
    public static void sendActions() {

        HashMap<Integer, ConcretePlan> currentPlans = new HashMap<>();
        HashMap<Integer, SendServerActionsEvent> currentSendServerActionsEvents = new HashMap<>();
        SendServerActionsEvent sendActionsEvent = null;
        ConflictService conflictService = new ConflictService();

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
            // Add the event to the HashMap, such that we can add it back to the queue later
            currentSendServerActionsEvents.put(
                    Integer.valueOf(sendActionsEvent.getAgent().getLabel()),
                    sendActionsEvent
            );
            // poll next element, without waiting
            sendActionsEvent = sendServerActionsQueue.poll();
        }

        // we should now have emptied the queue

        HashMap<Integer, ConcreteAction> agentsActions = new HashMap<>();
        // pop the next action from each plan
        currentPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        List<Conflict> conflictingAgents = conflictService.detectConflicts(currentPlans);
        if (!conflictingAgents.isEmpty()) {

            conflictingAgents.forEach(conflict -> {
                ResolvedConflict resolvedConflict = conflictService.resolveConflict(conflict);

                currentPlans.put(
                        Integer.valueOf(conflict.getConceder().getLabel()),
                        resolvedConflict.getConflictPlans().get(Integer.valueOf(conflict.getConceder().getLabel()))
                );

                currentPlans.put(
                        Integer.valueOf(conflict.getInitiator().getLabel()),
                        resolvedConflict.getConflictPlans().get(Integer.valueOf(conflict.getInitiator().getLabel()))
                );
            });
        }

        // send actions to server
        send(buildActionSet(agentsActions));

        // update the GlobalLevelService with this action
        agentsActions.forEach((agentNumber, concreteAction) -> {
            String agentLabel = agentNumber.toString();
            GlobalLevelService.getInstance().applyAction(
                    GlobalLevelService.getInstance().getAgent(agentLabel),
                    concreteAction
            );
        });

        // add plans back into the stack - they are now missing an action each
        currentPlans.forEach((agentNumber, concretePlan) -> {
            SendServerActionsEvent sendServerActionsEvent = currentSendServerActionsEvents.get(agentNumber);
            if (concretePlan.getActions().size() != 0) {
                // Add plan if it has at least 1 move left
                sendServerActionsEvent.setConcretePlan(concretePlan);
                sendServerActionsQueue.add(sendServerActionsEvent);
            } else {
                sendServerActionsEvent.setResponse(true);
            }
        });

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
            // System.err.format("Lost contact with the server. We stop now");
            // System.exit(1);
        }
        else if (response.contains("false")) {
            System.err.format("Server responded with %s to: %s\n", response, toServer);
        }
        else if (response.equals("success")) {
            // Pretend problem is solved
            EventBusService.post(new ProblemSolvedEvent());
        }
    }

    public static String buildActionSet(HashMap<Integer, ConcreteAction> agentsPlans) {
        return buildActionSet(agentsPlans, numberOfAgents);
    }

    public static String buildActionSet(HashMap<Integer, ConcreteAction> agentsActions, int numberOfAgents) {
        // take one action from each plan and combine them
        StringJoiner toServerBuilder = new StringJoiner(",", "[", "]");

        // Java 8 is awesome
        IntStream.range(0, numberOfAgents).forEach(agentNumber -> {
            ConcreteAction agentAction = agentsActions.get(agentNumber);
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