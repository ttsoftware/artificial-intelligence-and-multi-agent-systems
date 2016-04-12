package dtu.agency;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.agent.AgentThread;
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
import dtu.agency.planners.ConcretePlan;
import dtu.agency.services.EventBusService;
import dtu.agency.services.GlobalLevelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Agency implements Runnable {

    public Agency(Level level) {
        GlobalLevelService.getInstance().setLevel(level);
    }

    @Override
    public void run() {

        List<String> agentLabels = new ArrayList<>();

        GlobalLevelService.getInstance().getLevel().getAgents().forEach(agent -> {
            System.err.println("Starting agent: " + agent.getLabel());

            // Start a new thread (agent) for each plan
            EventBusService.execute(new AgentThread(agent));

            agentLabels.add(agent.getLabel());
        });

        // Register for self-handled events
        EventBusService.register(this);

        // Offer goals to agents
        // Each goalQueue is independent of one another so we can parallelStream
        GlobalLevelService.getInstance().getLevel().getGoalQueues().parallelStream().forEach(goalQueue -> {

            Goal goal;
            // we can poll(), since we know all estimations have finished
            while ((goal = goalQueue.poll()) != null) {

                // Register for incoming goal estimations
                GoalEstimationEventSubscriber goalEstimationSubscriber = new GoalEstimationEventSubscriber(goal, agentLabels.size());
                EventBusService.register(goalEstimationSubscriber);

                // offer the goal
                System.err.println("Offering goal: " + goal.getLabel());
                EventBusService.post(new GoalOfferEvent(goal));

                // Get the goal estimations and assign goals (blocks)
                String bestAgent = goalEstimationSubscriber.getBestAgent();

                System.err.println("Assigning goal " + goalEstimationSubscriber.getGoal().getLabel() + " to " + bestAgent);
                EventBusService.post(new GoalAssignmentEvent(bestAgent, goalEstimationSubscriber.getGoal()));
            }
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void planOfferEventSubscriber(PlanOfferEvent event) {

        System.err.println("Received offer for " + event.getGoal().getLabel() + " from " + event.getAgent().getLabel());

        EventBusService.post(new SendServerActionsEvent(event.getAgent(), event.getPlan()));
    }

    @Subscribe
    public void problemSolvedEventSubscriber(ProblemSolvedEvent event) {
        // wait for all threads to finish
        EventBusService.getThreads().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });
    }

    @Subscribe
    public void detectConflictEventSubscriber(DetectConflictsEvent event) {
        GlobalLevelService levelService = GlobalLevelService.getInstance();
        HashMap<Position, Integer> seer = new HashMap<>();
        List<Integer> conflictingAgents = new ArrayList<>();

        event.getCurrentPlans().forEach((agentNumber, concretePlan) -> {
            ConcreteAction action = concretePlan.getActions().getFirst();
            switch (action.getType()) {
                case MOVE:
                    MoveConcreteAction moveAction = (MoveConcreteAction) action;
                    Position newMoveAgentPosition = levelService.getAdjacentPositionInDirection(moveAction.getAgentPosition(),
                                                                                                moveAction.getAgentDirection());
                    if (!seer.containsKey(newMoveAgentPosition)) {
                        seer.put(moveAction.getAgentPosition(), agentNumber);
                        seer.put(newMoveAgentPosition, agentNumber);
                    }
                    else {
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
                    }
                    else {
                        conflictingAgents.add(seer.get(newPullAgentPosition));
                        conflictingAgents.add(agentNumber);
                    }

                    break;
                case PUSH:
                    PushConcreteAction pushAction = (PushConcreteAction) action;
                    Position newPushAgentPosition = levelService.getPosition(pushAction.getBox());
                    Position newPushBoxPosition = levelService.getAdjacentPositionInDirection(newPushAgentPosition,
                                                                                              pushAction.getBoxDirection());
                    if (!seer.containsKey(newPushAgentPosition)) {
                        seer.put(levelService.getPosition(agentNumber.toString()), agentNumber);
                        seer.put(newPushBoxPosition, agentNumber);
                        seer.put(newPushAgentPosition, agentNumber);
                    }
                    else {
                        conflictingAgents.add(seer.get(newPushAgentPosition));
                        conflictingAgents.add(agentNumber);
                    }

                    break;
                default:
            }
        });

        event.setResponse(false);
        if (conflictingAgents.size() > 0) {
            event.setResponse(true);
        }
    }
}