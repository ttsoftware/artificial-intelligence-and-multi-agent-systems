package dtu.agency.services;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.conflicts.Conflict;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.*;
import java.util.stream.IntStream;

public class ConflictService {

    /**
     * Takes all plans and checks whether all moves 1 step in the future will
     * cause conflicts.
     *
     * @param currentPlans contains a hashmap of agentNumber-plan pairs
     */
    public List<Conflict> detectConflicts(HashMap<Integer, ConcretePlan> currentPlans) {

        if (GlobalLevelService.getInstance().getLevel().getAgents().size() == 1) {
            return new ArrayList<>();
        }

        // The seer keeps track of which future positions are occupied.
        HashMap<Position, Integer> occupiedPositions = new HashMap<>();
        List<Conflict> conflictingAgents = new ArrayList<>();

        GlobalLevelService.getInstance().getLevel().getAgents().stream().forEach((agent) ->
                occupiedPositions.put(
                        GlobalLevelService.getInstance().getPosition(agent),
                        agent.getNumber()
                )
        );
        currentPlans.forEach((agentNumber, concretePlan) -> {
            List<ConcreteAction> actions = concretePlan.getActions();
            if (!actions.isEmpty()) {
                ConcreteAction action = actions.get(0);
                Position currentAgentPosition = GlobalLevelService.getInstance().getPosition(
                        GlobalLevelService.getInstance().getAgent(agentNumber)
                );

                for (int i = 1; i < actions.size() && action.getType().equals(ConcreteActionType.NONE); i++) {
                    action = actions.get(i);
                }

                if (action != null) {
                    if (action.getType().equals(ConcreteActionType.PUSH)) {
                        occupiedPositions.put(
                                GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                        currentAgentPosition,
                                        action.getAgentDirection()
                                ),
                                agentNumber
                        );
                    } else if (action.getType().equals(ConcreteActionType.PULL)) {
                        occupiedPositions.put(
                                GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                        currentAgentPosition,
                                        ((PullConcreteAction) action).getBoxMovingDirection().getInverse()
                                ),
                                agentNumber
                        );
                    }
                }
            }
        });

        currentPlans.forEach((agentNumber, concretePlan) -> {
            List<ConcreteAction> actions = concretePlan.getActions();
            if (!actions.isEmpty()) {
                // If plan is not empty, check for conflicts

                ConcreteAction action = actions.get(0);
                Position currentAgentPosition = GlobalLevelService.getInstance().getPosition(
                        GlobalLevelService.getInstance().getAgent(agentNumber)
                );

                switch (action.getType()) {
                    case MOVE:
                        MoveConcreteAction moveAction = (MoveConcreteAction) action;

                        // Find the new position of the agent based on the move action
                        Position newMoveAgentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                currentAgentPosition,
                                moveAction.getAgentDirection()
                        );

                        if (!occupiedPositions.containsKey(newMoveAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new position
                            occupiedPositions.put(newMoveAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                    new Conflict(
                                            occupiedPositions.get(newMoveAgentPosition),
                                            currentPlans.get(occupiedPositions.get(newMoveAgentPosition)),
                                            agentNumber,
                                            concretePlan
                                    )
                            );
                        }
                        break;
                    case PULL:
                        PullConcreteAction pullAction = (PullConcreteAction) action;

                        // Find the new position of the box, which is the agent's position as it is a pull action
                        Position newPullBoxPosition = GlobalLevelService.getInstance().getPosition(
                                GlobalLevelService.getInstance().getAgent(agentNumber)
                        );
                        // Find the new position of the agent based on the pull action
                        Position newPullAgentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                newPullBoxPosition,
                                pullAction.getAgentDirection()
                        );

                        if (!occupiedPositions.containsKey(newPullAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new positions of both agent and box
                            occupiedPositions.put(newPullBoxPosition, agentNumber);
                            occupiedPositions.put(newPullAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                    new Conflict(
                                            occupiedPositions.get(newPullAgentPosition),
                                            currentPlans.get(occupiedPositions.get(newPullAgentPosition)),
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

                        if (!occupiedPositions.containsKey(newPushBoxPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new positions of both agent and box
                            occupiedPositions.put(newPushBoxPosition, agentNumber);
                            occupiedPositions.put(newPushAgentPosition, agentNumber);
                        } else {
                            // If the seer knows the new position, it is in Conflict

                            // Add the conflicting agents to the conflictingAgents list
                            conflictingAgents.add(
                                    new Conflict(
                                            occupiedPositions.get(newPushBoxPosition),
                                            currentPlans.get(occupiedPositions.get(newPushBoxPosition)),
                                            agentNumber,
                                            concretePlan
                                    )
                            );
                        }

                        break;
                    default:
                        break;
                }
            }
        });

        return conflictingAgents;
    }

    /**
     * Takes a conflict and makes new plans for the agents so that they
     * will avoid conflicting.
     *
     * @param conflict to be solved
     * @return ResolvedConflict containing plans that solve the conflict
     */
    public ResolvedConflict resolveConflict(Conflict conflict) {

        System.err.print("I am " + BDIService.getInstance().getAgent() + ", and we are in conflict resolution!\n");

        boolean pushOrPull;
        List<ConcreteAction> actions = conflict.getInitiatorPlan().getActions();
        ConcreteAction conflictingAction;

        // Check if the initiator is carrying a box (next non-NoOp action is a push or pull
        if (!actions.isEmpty()) {
            conflictingAction = actions.get(0);

            for (int i = 1; i < actions.size() && conflictingAction.getType().equals(ConcreteActionType.NONE); i++) {
                conflictingAction = actions.get(i);
            }

            // Determine if the conflicting action is of type Push or Pull
            pushOrPull = (conflictingAction.getType() == ConcreteActionType.PUSH ||
                    conflictingAction.getType() == ConcreteActionType.PULL);
        } else {
            conflictingAction = conflict.getConcederPlan().getActions().get(0);
            pushOrPull = false;
        }

        // Find parking spaces
        List<Position> parkingSpaces = getParkingPositions(conflict, pushOrPull);

        // If we actually found parking spaces
        if (!parkingSpaces.isEmpty()) {
            if (pushOrPull) {
                return getResolvedConflictForAgentAndBox(
                        conflict,
                        parkingSpaces,
                        ((MoveBoxConcreteAction) conflictingAction).getBox()
                );

            } else {
                return getResolvedConflictForAgent(conflict, parkingSpaces);
            }
        }

        // There are no parking spaces. Can't solve conflict
        return null;
    }

    /**
     *
     *
     * @param conflict
     * @param pushOrPull
     * @return
     */
    public List<Position> getParkingPositions (Conflict conflict, boolean pushOrPull) {

        // Initializes list of parking spaces
        List<Position> parkingSpaces = new ArrayList<>();

        // Initializees new PlanningLevelService
        PlanningLevelService planningLevelService = new PlanningLevelService(
                BDIService.getInstance().getBDILevelService().getLevelClone()
        );

        // Finds the conceder's ordered path with box
        LinkedList<Position> orderedConcederPath = planningLevelService.getOrderedPathWithBox(
                (PrimitivePlan) conflict.getConcederPlan(),
                conflict.getConceder()
        );

        // Finds the initiator's ordered path with box
        LinkedList<Position> orderedInitiatorPath = planningLevelService.getOrderedPathWithBox(
                (PrimitivePlan) conflict.getInitiatorPlan(),
                conflict.getInitiator()
        );


        // Merges the conceder's path with the initiator's path
        LinkedList<Position> orderedPath = BDIService.getInstance().getBDILevelService().mergePaths(
                orderedConcederPath,
                orderedInitiatorPath
        );

        // Initializes MoveBoxActions for conceder and initiator
        MoveBoxConcreteAction moveConcedersBoxConcreteAction;
        MoveBoxConcreteAction moveInitiatorsBoxConcreteAction;

        // Finds the index of the first push or pull action for both conceder and initiator
        int indexOfFirstPushOrPullOfConceder = getIndexOfFirstPushOrPull((PrimitivePlan) conflict.getConcederPlan());
        int indexOfFirstPushOrPullOfInitiator = getIndexOfFirstPushOrPull((PrimitivePlan) conflict.getInitiatorPlan());

        // If initiator's plan is NOT empty, and it contains a push or pull action,
        // we extract the box and removes it from the level
        if (!((PrimitivePlan) conflict.getInitiatorPlan()).getActions().isEmpty()) {
            if (indexOfFirstPushOrPullOfInitiator < conflict.getInitiatorPlan().getActions().size()) {
                moveInitiatorsBoxConcreteAction = (MoveBoxConcreteAction) ((PrimitivePlan) conflict.getInitiatorPlan())
                        .getActions().get(indexOfFirstPushOrPullOfInitiator);

                planningLevelService.removeBox(moveInitiatorsBoxConcreteAction.getBox());
            }
        }

        // If conceder's plan is NOT empty, and it contains a push or pull action,
        // we extract the box and removes it from the level
        if (!((PrimitivePlan) conflict.getConcederPlan()).getActions().isEmpty()) {
            if (indexOfFirstPushOrPullOfConceder < conflict.getConcederPlan().getActions().size()) {
                moveConcedersBoxConcreteAction = (MoveBoxConcreteAction) ((PrimitivePlan) conflict.getConcederPlan())
                        .getActions().get(indexOfFirstPushOrPullOfConceder);

                planningLevelService.removeBox(moveConcedersBoxConcreteAction.getBox());
            }
        }

        // Save the initiator's original position
        Position initiatorPosition = planningLevelService.getPosition(conflict.getInitiator());

        // Remove initiator and conceder from the planningLevelService
        planningLevelService.removeAgent(conflict.getConceder());
        planningLevelService.removeAgent(conflict.getInitiator());

        // Find parking spaces
        if (pushOrPull) {
            // If pushOrPull, we find a parking space of depth 2
            parkingSpaces.add(planningLevelService.getFreeNeighbour(
                    orderedPath,
                    BDIService.getInstance().getBDILevelService().getPosition(conflict.getInitiator()),
                    2
            ));

            // Find all potential parking spaces neighbouring the parking space we found before
            HashSet<Position> potentialParkingSpaces = planningLevelService.getFreeNeighbourSet(parkingSpaces.get(0));

            // Find the second parking space that is adjacent to, but not on conceder's path
            for (Position potentialParkingSpace : potentialParkingSpaces) {

                if (!orderedConcederPath.contains(potentialParkingSpace)) {

                    for (Position pathPosition : orderedConcederPath) {

                        if (pathPosition.isAdjacentTo(potentialParkingSpace)) {

                            parkingSpaces.add(potentialParkingSpace);
                            break;
                        }
                    }
                }
            }

            // If we haven't found a second parking space near the first one, find any other parking space
            if (parkingSpaces.size() < 2) {
                parkingSpaces.add(
                        planningLevelService.getFreeNeighbour(
                                orderedPath,
                                initiatorPosition,
                                1
                        )
                );
            }
        } else {
            // we only need one parking space
            parkingSpaces.add(
                    planningLevelService.getFreeNeighbour(
                            orderedPath,
                            initiatorPosition,
                            1
                    )
            );
        }

        return parkingSpaces;
    }

    /**
     * Make plans and construct a ResolvedConflict in the case where the initiator doesn't have a box
     *
     * @param conflict
     * @param parkingSpaces
     * @return
     */
    public ResolvedConflict getResolvedConflictForAgent(Conflict conflict, List<Position> parkingSpaces) {
        // Construct RGotoAction for moving the initiator away
        RGotoAction outOfTheWayAction = new RGotoAction(parkingSpaces.get(0));

        // Construct RGotoAction for moving the initiator back to his original position
        RGotoAction moveBackAction = new RGotoAction(
                BDIService.getInstance().getBDILevelService().getPosition(
                        conflict.getInitiator().getLabel()
                )
        );

        // Construct HTNPlanner for outOfTheWayAction
        HTNPlanner htnPlanner = new HTNPlanner(
                new PlanningLevelService(
                        BDIService.getInstance().getBDILevelService().getLevelClone()
                ),
                outOfTheWayAction,
                RelaxationMode.NoAgentsNoBoxes
        );

        // Find plan for moving out of the way
        PrimitivePlan outOfTheWayPlan = htnPlanner.plan();

        // Move the initiator to its new position so it can plan a way back.
        PlanningLevelService planningLevelService = new PlanningLevelService(
                BDIService.getInstance().getBDILevelService().getLevelClone()
        );

        // Removes the initiator from the planningLevelService
        planningLevelService.removeAgent(conflict.getInitiator());

        // Inserts the initiator at its parking space
        planningLevelService.insertAgent(
                conflict.getInitiator(),
                parkingSpaces.get(0)
        );

        // Construct HTNPlanner for moveBackAction
        htnPlanner = new HTNPlanner(
                planningLevelService,
                moveBackAction,
                RelaxationMode.NoAgentsNoBoxes
        );

        // return ResolvedConflict
        return getResolvedConflict(outOfTheWayPlan, htnPlanner.plan(), conflict);
    }

    /**
     * Make plans and construct a ResolvedConflict for the case where the initiator has a box
     *
     * @param conflict
     * @param parkingSpaces
     * @param box
     * @return ResolvedConflict
     */
    public ResolvedConflict getResolvedConflictForAgentAndBox(Conflict conflict, List<Position> parkingSpaces, Box box) {

        // Save the original position of the agent
        Position agentPositionOrigin = BDIService.getInstance().getBDILevelService().
                getPosition(conflict.getInitiator().getLabel());

        // Save the original position of the box
        Position boxPositionOrigin = BDIService.getInstance().getBDILevelService().
                getPosition(box);

        // Initialize a PlanningLevelService
        PlanningLevelService planningLevelService = new PlanningLevelService(
                BDIService.getInstance().getBDILevelService().getLevelClone()
        );

        // Make plan 1 with parking spaces in found order
        PrimitivePlan outOfTheWayPlan1 = getPlanToMoveBoxAndAgent(
                box,
                parkingSpaces.get(0), // boxDestination
                parkingSpaces.get(1), // agentDestination
                planningLevelService
        );

        // Make plan 2 with parking spaces in reverse order
        PrimitivePlan outOfTheWayPlan2 = getPlanToMoveBoxAndAgent(
                box,
                parkingSpaces.get(1), // boxDestination
                parkingSpaces.get(0), // agentDestination
                planningLevelService
        );

        // Initialize final outOfTheWayPlan
        PrimitivePlan outOfTheWayPlan;

        // Remove initiator and box from the planningLevelService
        planningLevelService.removeAgent(conflict.getInitiator());
        planningLevelService.removeBox(box);

        if (outOfTheWayPlan1 == null) {
            // If outOfTheWayPlan1 is null, insert initiator and box in the appropriate parking spaces
            // and set final outOfTheWayPlan to outOfTheWayPlan2

            planningLevelService.insertAgent(conflict.getInitiator(), parkingSpaces.get(0));
            planningLevelService.insertBox(box, parkingSpaces.get(1));

            outOfTheWayPlan = new PrimitivePlan(outOfTheWayPlan2);
        } else if (outOfTheWayPlan2 == null) {
            // If outOfTheWayPlan1 is null, insert initiator and box in the appropriate parking spaces
            // and set final outOfTheWayPlan to outOfTheWayPlan1

            planningLevelService.insertAgent(conflict.getInitiator(), parkingSpaces.get(1));
            planningLevelService.insertBox(box, parkingSpaces.get(0));

            outOfTheWayPlan = new PrimitivePlan(outOfTheWayPlan1);
        } else if (outOfTheWayPlan1.size() < outOfTheWayPlan2.size()) {
            // If outOfTheWayPlan1 is smaller then outOfTheWayPlan2, insert initiator and box in the
            // appropriate parking spaces and set final outOfTheWayPlan to outOfTheWayPlan1

            planningLevelService.insertAgent(conflict.getInitiator(), parkingSpaces.get(1));
            planningLevelService.insertBox(box, parkingSpaces.get(0));

            outOfTheWayPlan = new PrimitivePlan(outOfTheWayPlan1);
        } else {
            // If outOfTheWayPlan1 is larger or equal to outOfTheWayPlan2, insert initiator and box in the
            // appropriate parking spaces and set final outOfTheWayPlan to outOfTheWayPlan2

            planningLevelService.insertAgent(conflict.getInitiator(), parkingSpaces.get(0));
            planningLevelService.insertBox(box, parkingSpaces.get(1));

            outOfTheWayPlan = new PrimitivePlan(outOfTheWayPlan2);
        }

        // Make plan for moving the initiator and box back to their original positions
        PrimitivePlan moveBackPlan = getPlanToMoveBoxAndAgent(
                box,
                boxPositionOrigin,
                agentPositionOrigin,
                planningLevelService
        );

        // Return resolvedConflict
        return getResolvedConflict(outOfTheWayPlan, moveBackPlan, conflict);
    }

    /**
     * Makes a plan to move a box to boxDestionation and the agent to agentDestination
     *
     * @param box
     * @param boxDestination
     * @param agentDestination
     * @param planningLevelService
     * @return PrimitivePlan
     */
    public PrimitivePlan getPlanToMoveBoxAndAgent(Box box, Position boxDestination, Position agentDestination, PlanningLevelService planningLevelService) {

        // Initializes a new HMoveBoxAction
        HMoveBoxAction moveBoxOutOfTheWayAction = new HMoveBoxAction(
                box,
                boxDestination,
                agentDestination
        );

        // Initializes a htn planner with the HMoveBoxAction
        HTNPlanner htnPlanner = new HTNPlanner(
                planningLevelService,
                moveBoxOutOfTheWayAction,
                RelaxationMode.NoAgentsNoBoxes // We want to be able to plan through boxes and agents
        );

        // Returns plan
        return htnPlanner.plan();
    }

    /**
     * Takes the outOfTheWayPlan and appends NoOps and the moveBackPlan if appropriate. Also prepends NoOp to conceder.
     * Constructs and returns a ResolvedConflict object with the new plans.
     *
     * @param outOfTheWayPlan Plan for the initiator to move out of the conceder's path
     * @param moveBackPlan Plan for the initiator to move back to its original position
     * @param conflict
     * @return ResolvedConflict
     */
    public ResolvedConflict getResolvedConflict(PrimitivePlan outOfTheWayPlan, PrimitivePlan moveBackPlan, Conflict conflict) {

        // If the initiators original plan is NOT empty, add NoOps and a moveBackPlan
        if (!conflict.getInitiatorPlan().getActions().isEmpty()) {
            // Append 2 NoOps
            IntStream.range(0, 2).forEach((number) ->
                    outOfTheWayPlan.addAction(new NoConcreteAction())
            );

            // Append moveBackPlan
            outOfTheWayPlan.appendActions(moveBackPlan);
        }

        // Append the original initiator plan
        outOfTheWayPlan.appendActions((PrimitivePlan) conflict.getInitiatorPlan());

        // Initialize concederPlan
        PrimitivePlan concederPlan = new PrimitivePlan();

        // Add NoOp actions so that it waits until the initiator is away
        concederPlan.addAction(new NoConcreteAction());

        // Append the original conceder Plan
        concederPlan.appendActions((PrimitivePlan) conflict.getConcederPlan());

        System.err.println("We have just solved the conflict between agents " + conflict.getInitiator().getLabel()
                + " and agent " + conflict.getConceder().getLabel());
        System.err.println("The plans for conlict resolution are: ");
        System.err.println(outOfTheWayPlan.getActions().toString());
        System.err.println(concederPlan.getActions().toString());

        // Return new resolved Conflict
        return new ResolvedConflict(
                conflict.getInitiator(),
                outOfTheWayPlan,
                BDIService.getInstance().getBDILevelService().getPosition(conflict.getInitiator()),
                conflict.getConceder(),
                concederPlan,
                BDIService.getInstance().getBDILevelService().getPosition(conflict.getConceder())
        );
    }

    /**
     * Searches the plan for a PUSH or PULL action and returns its index.
     *
     * @param plan
     * @return Index of first PULL or PUSH action if one exists. Size of plan otherwise.
     */
    public int getIndexOfFirstPushOrPull(PrimitivePlan plan) {
        int i = 0;
        for (Action action : plan.getActions()) {
            if (action.getType().equals(ConcreteActionType.PUSH) ||
                    action.getType().equals(ConcreteActionType.PULL)) {
                break;
            }
            i++;
        }

        return i;
    }
}
