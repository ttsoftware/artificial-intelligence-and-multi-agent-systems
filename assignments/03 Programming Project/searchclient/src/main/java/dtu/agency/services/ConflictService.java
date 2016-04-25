package dtu.agency.services;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Position;
import dtu.agency.conflicts.Conflict;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.planners.plans.ConcretePlan;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConflictService {

    /**
     * Takes all plans and checks whether all moves 1 step in the future will
     * cause conflicts.
     *
     * @param currentPlans contains a hashmap of agentNumber-plan pairs
     */
    public List<Conflict> detectConflicts(HashMap<Integer, ConcretePlan> currentPlans) {

        // The seer keeps track of which future positions are occupied.
        HashMap<Position, Integer> seer = new HashMap<>();
        List<Conflict> conflictingAgents = new ArrayList<>();

        currentPlans.forEach((agentNumber, concretePlan) -> {
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
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new position
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newMoveAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                new Conflict(
                                    seer.get(newMoveAgentPosition),
                                    currentPlans.get(seer.get(newMoveAgentPosition)),
                                    agentNumber,
                                    concretePlan
                                )
                            );
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
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new positions of both agent and box
                            seer.put(GlobalLevelService.getInstance().getPosition(pullAction.getBox()), agentNumber);
                            seer.put(newPullBoxPosition, agentNumber);
                            seer.put(newPullAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                    new Conflict(
                                            seer.get(newPullAgentPosition),
                                            currentPlans.get(seer.get(newPullAgentPosition)),
                                            agentNumber,
                                            concretePlan
                                    )
                            );
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
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new positions of both agent and box
                            seer.put(currentAgentPosition, agentNumber);
                            seer.put(newPushBoxPosition, agentNumber);
                            seer.put(newPushAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                    new Conflict(
                                            seer.get(newPushAgentPosition),
                                            currentPlans.get(seer.get(newPushAgentPosition)),
                                            agentNumber,
                                            concretePlan
                                    )
                            );
                        }

                        break;
                    default:
                }
            }
        });

        return conflictingAgents;
    }

    public ResolvedConflict resolveConflict(Conflict conflict) {

        /* TODO actually resolve conflict
         *
         * The "fastest" agent (The one with the shortest plan) is the
         * conceder. It will concede the conflict handling to the "slowest"
         * agent, being the initiator.
         *
         * The initiator looks one by one at the positions in the conceder's path,
         * trying to find spots next to the path, where it can park, while
         * the conceder walks past the initiator.
         *
         * parkingSpotsNeeded <- 1
         * if initiator.lastAction is push/pull:
         *      parkingSpotsNeeded <- 2
         * Foreach position in concederPath:
         *      parkingSpots <- findFreeNeighbours(position)
         *      parkingSpot1 <- parkingSpots.get(0)
         *
         *      if parkingSpotsNeeded is 2:
         *          foreach parkingSpot in parkingSpots:
         *              additionalParkingSpots <- findFreeNeighbours(parkingSpot) (can not be position where it came from)
         *              if additionalParkingSpots is not empty:
         *                  parkingSpot1 <- parkingSpot
         *                  parkingSpot2 <- additionalParkingSpots
         *                  break
         *
         *       conflictPlan <- initiator.makePlan(GOTO(parkingSpot1, parkingSpot2)
         *       resetPlan <- initiator.makePlan(GOBACK(conflictPlan))
         */

        return null;
    }
}
