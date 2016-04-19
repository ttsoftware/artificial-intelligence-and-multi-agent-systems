package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.agent.AgentThread;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.events.client.DetectConflictsEvent;
import dtu.agency.events.client.SendServerActionsEvent;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalEstimationEventSubscriber;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.events.agent.ProblemSolvedEvent;
import dtu.agency.services.AgentService;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.ThreadService;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Agency implements Runnable {

    private static final Object synchronizer = new Object();

    public Agency(Level level) {
        GlobalLevelService.getInstance().setLevel(level);
    }

    @Override
    public void run() {
        List<Agent> agents = GlobalLevelService.getInstance().getLevel().getAgents();

        int numberOfAgents = agents.size();

        AgentService.getInstance().addAgents(agents);

        agents.forEach(agent -> {
            System.err.println(Thread.currentThread().getName() + ": Constructing agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            ThreadService.execute(new AgentThread());
        });

        // Register for self-handled events
        EventBusService.register(this);

        // Map of goal -> bestAgent
        HashMap<Goal, Agent> bestAgents = new HashMap<>();

        // Offer goals to agents
        // Each goalQueue is independent of one another so we can parallelStream
        GlobalLevelService.getInstance().getLevel().getGoalQueues().parallelStream().forEach(goalQueue -> {

            Goal goal;
            // we can poll(), since we know all estimations have finished
            while ((goal = goalQueue.poll()) != null) {

                // Register for incoming goal estimations
                GoalEstimationEventSubscriber goalEstimationSubscriber = new GoalEstimationEventSubscriber(goal, numberOfAgents);
                EventBusService.register(goalEstimationSubscriber);

                // offer the goal
                System.err.println("Offering goal: " + goal.getLabel());

                EventBusService.post(new GoalOfferEvent(goal));

                // Get the goal estimations (blocks current thread)
                bestAgents.put(goal, goalEstimationSubscriber.getBestAgent());
            }
        });

        // assign the goals
        bestAgents.entrySet().parallelStream().forEach(goalAgentEntry -> {
            Goal goal = goalAgentEntry.getKey();
            System.err.println("Assigning goal " + goal.getLabel() + " to " + goalAgentEntry.getValue());

            GoalAssignmentEvent goalAssignmentEvent = new GoalAssignmentEvent(goalAgentEntry.getValue(), goal);

            EventBusService.post(goalAssignmentEvent);

            // get the plan response (blocks current thread)
            // how long do we wish to wait for the agents to finish planning?
             /*
            ConcretePlan plan = goalAssignmentEvent.getResponse(2000);

            System.err.println("Received offer for " + goal.getLabel() + " from " + goalAgentEntry.getValue());

            EventBusService.post(new SendServerActionsEvent(goalAssignmentEvent.getAgent(), plan));
            */
        });

        try {
            // wait indefinitely until problem is solved
            synchronized (synchronizer) {
                synchronizer.wait();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        System.err.println("Agency is exiting.");
    }

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {
        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getAgent(), event.getPlan()));
    }

    @Subscribe
    public void detectConflictEventSubscriber(DetectConflictsEvent event) {
        GlobalLevelService levelService = GlobalLevelService.getInstance();
        HashMap<Position, Integer> seer = new HashMap<>();
        List<Integer> conflictingAgents = new ArrayList<>();

        event.getCurrentPlans().forEach((agentNumber, concretePlan) -> {
            List<ConcreteAction> actions = concretePlan.getActions();
            if (!actions.isEmpty()) {
                ConcreteAction action = actions.get(0);
                Position currentAgentPosition = levelService.getPosition(agentNumber.toString());

                switch (action.getType()) {
                    case MOVE:
                        MoveConcreteAction moveAction = (MoveConcreteAction) action;
                        Position newMoveAgentPosition = levelService.getAdjacentPositionInDirection(currentAgentPosition,
                                moveAction.getAgentDirection());
                        if (!seer.containsKey(newMoveAgentPosition)) {
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newMoveAgentPosition, agentNumber);
                        } else {
                            conflictingAgents.add(seer.get(newMoveAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }
                        break;
                    case PULL:
                        PullConcreteAction pullAction = (PullConcreteAction) action;
                        Position newPullBoxPosition = levelService.getPosition(agentNumber.toString());
                        Position newPullAgentPosition = levelService.getAdjacentPositionInDirection(newPullBoxPosition,
                                pullAction.getAgentDirection());
                        if (!seer.containsKey(newPullAgentPosition)) {
                            seer.put(levelService.getPosition(pullAction.getBox()), agentNumber);
                            seer.put(newPullBoxPosition, agentNumber);
                            seer.put(newPullAgentPosition, agentNumber);
                        } else {
                            conflictingAgents.add(seer.get(newPullAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }

                        break;
                    case PUSH:
                        PushConcreteAction pushAction = (PushConcreteAction) action;
                        Position newPushAgentPosition = levelService.getPosition(pushAction.getBox());
                        Position newPushBoxPosition = levelService.getAdjacentPositionInDirection(newPushAgentPosition,
                                pushAction.getBoxMovingDirection());
                        if (!seer.containsKey(newPushAgentPosition)) {
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newPushBoxPosition, agentNumber);
                            seer.put(newPushAgentPosition, agentNumber);
                        } else {
                            conflictingAgents.add(seer.get(newPushAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }

                        break;
                    default:
                }
            }
        });

        event.setResponse(false);
        if (conflictingAgents.size() > 0) {
            event.setResponse(true);
        }
    }

    @Subscribe
    public void problemSolvedEventSubscriber(ProblemSolvedEvent event) {
        // wait for all threads to finish
        ThreadService.shutdown();

        // allow this thread to be joined
        synchronized (synchronizer) {
            synchronizer.notify();
        }
    }
}