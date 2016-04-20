package dtu.agency.services;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.events.client.DetectConflictsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rasmus on 4/20/16.
 */
public class ConflictDetectionService {

    /**
     * Takes all plans and checks whether all moves 1 step in the future will
     * cause conflicts.
     *
     * @param event contains a list of agentNumber-plan pairs
     */
    @Subscribe
    public void detectConflictEventSubscriber(DetectConflictsEvent event) {

        // The seer keeps track of which future positions are occupied.
        HashMap<Position, Integer> seer = new HashMap<>();
        List<Integer> conflictingAgents = new ArrayList<>();

        event.getCurrentPlans().forEach((agentNumber, concretePlan) -> {
            List<ConcreteAction> actions = concretePlan.getActions();
            if (!actions.isEmpty()) {
                // If plan is not empty, check for conflicts

                ConcreteAction action = actions.get(0);
                Position currentAgentPosition = GlobalLevelService.getInstance().getPosition(agentNumber.toString());

                switch (action.getType()) {
                    case MOVE:
                        MoveConcreteAction moveAction = (MoveConcreteAction) action;

                        // Find the new position of the agent based on the move action
                        Position newMoveAgentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(currentAgentPosition,
                                moveAction.getAgentDirection());

                        if (!seer.containsKey(newMoveAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in conflict

                            // Tell seer about current and new position
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newMoveAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(seer.get(newMoveAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }
                        break;
                    case PULL:
                        PullConcreteAction pullAction = (PullConcreteAction) action;

                        // Find the new position of the box, which is the agent's position as it is a pull action
                        Position newPullBoxPosition = GlobalLevelService.getInstance().getPosition(agentNumber.toString());
                        // Find the new position of the agent based on the pull action
                        Position newPullAgentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(newPullBoxPosition,
                                pullAction.getAgentDirection());

                        if (!seer.containsKey(newPullAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in conflict

                            // Tell seer about current and new positions of both agent and box
                            seer.put(GlobalLevelService.getInstance().getPosition(pullAction.getBox()), agentNumber);
                            seer.put(newPullBoxPosition, agentNumber);
                            seer.put(newPullAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(seer.get(newPullAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }

                        break;
                    case PUSH:
                        PushConcreteAction pushAction = (PushConcreteAction) action;

                        // Find the new position of the agent, which is the box's position as it is a push action
                        Position newPushAgentPosition = GlobalLevelService.getInstance().getPosition(pushAction.getBox());
                        // Find the new position of the box based on the push action
                        Position newPushBoxPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(newPushAgentPosition,
                                pushAction.getBoxMovingDirection());

                        if (!seer.containsKey(newPushAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in conflict

                            // Tell seer about current and new positions of both agent and box
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newPushBoxPosition, agentNumber);
                            seer.put(newPushAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(seer.get(newPushAgentPosition));
                            conflictingAgents.add(agentNumber);
                        }

                        break;
                    default:
                }
            }
        });

        event.setResponse(conflictingAgents.size() > 0);
    }
}
