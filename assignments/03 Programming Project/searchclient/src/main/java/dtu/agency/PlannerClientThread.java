package dtu.agency;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import dtu.agency.conflicts.Conflict;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.events.agency.ProblemSolvedEvent;
import dtu.agency.events.client.ConflictResolutionEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.pop.GotoPOP;
import dtu.agency.services.ConflictService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class PlannerClientThread implements Runnable {

    private BufferedReader serverMessages;
    private int numberOfAgents;
    private ArrayBlockingQueue<SendServerActionsEvent> sendServerActionsQueue;
    private List<ResolvedConflict> resolvedConflicts = new ArrayList<>();

    // Thread for communicating with the server
    private Thread sendActionsThread = new Thread(this::sendActions);;

    @Override
    public void run() {

        serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = null;
        try {
            level = ProblemMarshaller.marshall(serverMessages);
        } catch (IOException e) {
            // We should safely be able to ignore this exception
        }

        // Create the level service
        GlobalLevelService.getInstance().setLevel(level);

        // Prioritize goals
        GotoPOP gotoPlanner = new GotoPOP();
        GlobalLevelService.getInstance().updatePriorityQueues(gotoPlanner.getWeighedGoals());

        numberOfAgents = level.getAgents().size();
        sendServerActionsQueue = new ArrayBlockingQueue<>(numberOfAgents);

        ThreadService.setNumberOfAgents(numberOfAgents);

        // Thread which actually communicates with the server
        sendActionsThread.start();

        // Register for events
        EventBusService.register(this);

        Thread agencyThread = new Thread(new Agency());
        agencyThread.start();
        try {
            agencyThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        System.err.println("Agency was joined.");
    }

    @Subscribe
    public void sendServerActionsEventSubscriber(SendServerActionsEvent event) {

        int actionCount = event.getConcretePlan().getActions().size();
        System.err.println("Received a plan from Agency with " + actionCount + " actions.");

        if (actionCount > 0) {
            try {
                sendServerActionsQueue.add(event);
            } catch (IllegalStateException e) {
                // We are trying to add more Stacks than agents
                e.printStackTrace(System.err);
            }
        }
        else {
            event.setResponse(true);
        }
    }

    @Subscribe
    public void problemSolvedEventSubscriber(ProblemSolvedEvent event) {

        System.err.println("We solved the entire level!");

        // Join when problem has been solved
        try {
            sendActionsThread.interrupt();
            sendActionsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Interact with the server. Pop the next stack of actions.
     */
    public boolean sendActions() {

        HashMap<Integer, ConcretePlan> currentPlans = new HashMap<>();
        HashMap<Integer, SendServerActionsEvent> currentSendServerActionsEvents = new HashMap<>();
        SendServerActionsEvent sendActionsEvent = null;
        ConflictService conflictService = new ConflictService();

        try {
            // .take() will call Thread.wait() until an element (Stack) becomes available
            sendActionsEvent = sendServerActionsQueue.take();
        } catch (InterruptedException e) {
            // we should be able to safely ignore this exception
        }

        // We take the next collection of plans from the queue
        while (sendActionsEvent != null) {
            currentPlans.put(
                    sendActionsEvent.getAgent().getNumber(),
                    sendActionsEvent.getConcretePlan()
            );
            // Add the event to the HashMap, such that we can add it back to the queue later
            currentSendServerActionsEvents.put(
                    sendActionsEvent.getAgent().getNumber(),
                    sendActionsEvent
            );
            // poll next element, without waiting
            sendActionsEvent = sendServerActionsQueue.poll();
        }

        List<Conflict> conflicts = conflictService.detectConflicts(currentPlans);
        if (!conflicts.isEmpty()) {

            conflicts.forEach(conflict -> {
                ResolvedConflict fakeResolvedConflict = new ResolvedConflict(
                        conflict.getInitiator(), conflict.getInitiatorPlan(), conflict.getConceder(), conflict.getConcederPlan());
                if (resolvedConflicts.contains(fakeResolvedConflict)) {
                    ResolvedConflict resolvedConflict = resolvedConflicts.get(resolvedConflicts.indexOf(fakeResolvedConflict));
                    if (conflict.getInitiator().equals(resolvedConflict.getInitiator())) {
                        Agent initiator = conflict.getInitiator();
                        ConcretePlan initiatorPlan = conflict.getInitiatorPlan();

                        conflict.setInitiator(conflict.getConceder());
                        conflict.setConceder(initiator);

                        conflict.setInitiatorPlan(conflict.getConcederPlan());
                        conflict.setConcederPlan(initiatorPlan);
                    }
                }

                ConflictResolutionEvent conflictResolutionEvent = new ConflictResolutionEvent(conflict);
                EventBusService.post(conflictResolutionEvent);

                ResolvedConflict resolvedConflict = conflictResolutionEvent.getResponse();

                if (resolvedConflict == null) {
                    // if the first Agent could not resolve the conflict
                    Conflict switchedConflict = conflictService.switchConcederAndInitiator(conflict);
                    ConflictResolutionEvent switchedConflictResolutionEvent = new ConflictResolutionEvent(switchedConflict);

                    EventBusService.post(switchedConflictResolutionEvent);
                    resolvedConflict = conflictResolutionEvent.getResponse();

                    if (resolvedConflict == null) {
                        throw new RuntimeException("No one can solve this conflict");
                    }
                }

                resolvedConflicts.add(resolvedConflict);

                // add the plan for resolving the conflict to the two agents plans
                currentPlans.put(
                        conflict.getConceder().getNumber(),
                        resolvedConflict.getConcederPlan()
                );

                currentPlans.put(
                        conflict.getInitiator().getNumber(),
                        resolvedConflict.getInitiatorPlan()
                );

                // add 'fake' SendServerActionsEvents containing the new plans
                sendServerActionsQueue.add(new SendServerActionsEvent(
                        resolvedConflict.getConceder(),
                        resolvedConflict.getConcederPlan()
                ));

                sendServerActionsQueue.add(new SendServerActionsEvent(
                        resolvedConflict.getInitiator(),
                        resolvedConflict.getInitiatorPlan()
                ));
            });

            return sendActions();
        }

        // we should now have emptied the queue
        HashMap<Integer, ConcreteAction> agentsActions = new HashMap<>();
        // pop the next action from each plan
        currentPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        // send actions to server
        send(buildActionSet(agentsActions));

        // update the GlobalLevelService with this action
        agentsActions.forEach((agentNumber, concreteAction) -> {
            GlobalLevelService.getInstance().applyAction(
                    GlobalLevelService.getInstance().getAgent(agentNumber),
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
        return sendActions();
    }

    public void send(String toServer) {
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
        } else if (response.contains("false")) {
            System.err.format("Server responded with %s to: %s\n", response, toServer);
        } else if (response.equals("success")) {
            // Pretend problem is solved
            EventBusService.post(new ProblemSolvedEvent());
        }
    }

    public String buildActionSet(HashMap<Integer, ConcreteAction> agentsPlans) {
        return buildActionSet(agentsPlans, numberOfAgents);
    }

    public String buildActionSet(HashMap<Integer, ConcreteAction> agentsActions, int numberOfAgents) {
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