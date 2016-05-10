package dtu.agency.services;

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

                if (action.getType().equals(ConcreteActionType.NONE)) {
                    actions.stream().forEach((planAction) -> {
                        if (!planAction.getType().equals(ConcreteActionType.MOVE)) {
                            return;
                        }
                        if (planAction.getType().equals(ConcreteActionType.PUSH)) {
                            occupiedPositions.put(
                                    GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                            currentAgentPosition,
                                            planAction.getAgentDirection()
                                    ),
                                    agentNumber
                            );
                        } else if (planAction.getType().equals(ConcreteActionType.PULL)) {
                            occupiedPositions.put(
                                    GlobalLevelService.getInstance().getAdjacentPositionInDirection(
                                            currentAgentPosition,
                                            ((PullConcreteAction) planAction).getBoxMovingDirection()
                                    ),
                                    agentNumber
                            );
                        }
                    });
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
                            occupiedPositions.put(currentAgentPosition, agentNumber);
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
                        Position newPullBoxPosition = GlobalLevelService.getInstance().getPosition(agentNumber.toString());
                        // Find the new position of the agent based on the pull action
                        Position newPullAgentPosition = GlobalLevelService.getInstance().getAdjacentPositionInDirection(newPullBoxPosition,
                                pullAction.getAgentDirection());

                        if (!occupiedPositions.containsKey(newPullAgentPosition)) {
                            // If the seer doesn't know the new position, it is not (yet) in Conflict

                            // Tell seer about current and new positions of both agent and box
                            occupiedPositions.put(GlobalLevelService.getInstance().getPosition(pullAction.getBox()), agentNumber);
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
                            occupiedPositions.put(currentAgentPosition, agentNumber);
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

        System.err.print("We are in conflict resolution!\n");

        // Save the conflicting action
        boolean pushOrPull;
        ConcreteAction conflictingAction;

        if(!conflict.getInitiatorPlan().getActions().isEmpty()) {
            conflictingAction = conflict.getInitiatorPlan().getActions().get(0);

            // Determine if the conflicting action is of type Push or Pull
            pushOrPull = (conflictingAction.getType() == ConcreteActionType.PUSH ||
                    conflictingAction.getType() == ConcreteActionType.PULL);
        } else {
            conflictingAction = conflict.getConcederPlan().getActions().get(0);
            pushOrPull = false;
        }

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
        } else {
            // TODO: Try again with initiator and conceder switched

//            // Switch conceder and initiator
//            conflict = switchConcederAndInitiator(conflict);
//
//            conflictingAction = conflict.getConcederPlan().getActions().get(0);
//
//            // Determine if the conflicting action is of type Push or Pull
//            pushOrPull = (conflictingAction.getType() == ConcreteActionType.PUSH ||
//                    conflictingAction.getType() == ConcreteActionType.PULL);
//
//            parkingSpaces = getParkingPositions(conflict, pushOrPull);
//
//            if (!parkingSpaces.isEmpty()) {
//                return getResolveConflictForConcederAndConflictingAction(((MoveBoxConcreteAction) conflictingAction).getBox(), conflict,
//                        pushOrPull, parkingSpaces);
//            }

        }

        return null;
    }

    public List<Position> getParkingPositions (Conflict conflict, boolean pushOrPull) {
        List<Position> parkingSpaces = new ArrayList<>();

        PlanningLevelService planningLevelService = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevelClone());

        LinkedList<Position> orderedConcederPath = planningLevelService.getOrderedPathWithBox(
                (PrimitivePlan) conflict.getConcederPlan()
        );

        LinkedList<Position> orderedInitiatorPath = planningLevelService.getOrderedPathWithBox(
                (PrimitivePlan) conflict.getInitiatorPlan()
        );

        LinkedList<Position> orderedPath = BDIService.getInstance().getBDILevelService().mergePaths(orderedConcederPath, orderedInitiatorPath);

        ConcreteActionType actionType = null;
        MoveBoxConcreteAction moveBoxConcreteAction = null;

        if(!((PrimitivePlan) conflict.getConcederPlan()).getActions().isEmpty()) {
            actionType = ((PrimitivePlan) conflict.getConcederPlan()).getActions().get(0).getType();

            if (actionType.equals(ConcreteActionType.PUSH) || actionType.equals(ConcreteActionType.PULL)) {
                moveBoxConcreteAction = (MoveBoxConcreteAction) ((PrimitivePlan) conflict.getConcederPlan()).getActions().get(0);

                orderedPath.addFirst(BDIService.getInstance().getBDILevelService().getPosition(
                        moveBoxConcreteAction.getBox()
                ));

                planningLevelService.removeBox(moveBoxConcreteAction.getBox());
            }
        }


        orderedPath.addFirst(BDIService.getInstance().getBDILevelService().getPosition(conflict.getConceder()));

        planningLevelService.removeAgent(conflict.getConceder());

        // Find parking spaces
        parkingSpaces.add(
                planningLevelService.getFreeNeighbour(
                        orderedPath,
                        planningLevelService.getPosition(conflict.getInitiator()),
                        planningLevelService.getPosition(conflict.getInitiator()),
                        1
                )
        );

        // Find parking spaces
        if(pushOrPull) {
            parkingSpaces.add(
                    BDIService.getInstance().getBDILevelService().getFreeNeighbour(
                            orderedPath,
                            BDIService.getInstance().getBDILevelService().getPosition(conflict.getInitiator()),
                            BDIService.getInstance().getBDILevelService().getPosition(conflict.getConceder()),
//                            BDIService.getInstance().getBDILevelService().getPosition(
//                                    ((MoveBoxConcreteAction) conflict.getConcederPlan().getActions().get(0)).getBox()
//                            ),
                            2
                    )
            );
        }

        if(!((PrimitivePlan) conflict.getConcederPlan()).getActions().isEmpty()) {
            if (actionType.equals(ConcreteActionType.PUSH) || actionType.equals(ConcreteActionType.PULL)) {
                planningLevelService.insertBox(moveBoxConcreteAction.getBox(),
                        BDIService.getInstance().getBDILevelService().getPosition(moveBoxConcreteAction.getBox()));
            }
        }

        planningLevelService.insertAgent(conflict.getConceder(),
                BDIService.getInstance().getBDILevelService().getPosition(conflict.getConceder()));

        return parkingSpaces;
    }

    public Conflict switchConcederAndInitiator (Conflict conflict) {
        Agent conceder = conflict.getConceder();
        Agent initiator = conflict.getInitiator();
        ConcretePlan concederPlan = conflict.getConcederPlan();
        ConcretePlan initiatorPlan = conflict.getInitiatorPlan();

        conflict.setConceder(initiator);
        conflict.setInitiator(conceder);
        conflict.setConcederPlan(initiatorPlan);
        conflict.setInitiatorPlan(concederPlan);

        return conflict;
    }

    public ResolvedConflict getResolvedConflictForAgent(Conflict conflict, List<Position> parkingSpaces)
    {
        // Construct action for moving the initiator away
        RGotoAction outOfTheWayAction = new RGotoAction(parkingSpaces.get(0));
        // Construct action for moving the initiator back to his original position
        RGotoAction moveBackAction = new RGotoAction(
                BDIService.getInstance().getBDILevelService().getPosition(
                        conflict.getInitiator().getLabel()
                )
        );

        // Construct HTNPlanner for outOfTheWayAction
        HTNPlanner htnPlanner = new HTNPlanner(
                new PlanningLevelService(
                        BDIService.getInstance().getBDILevelService().getLevel()
                ),
                outOfTheWayAction,
                RelaxationMode.NoAgentsNoBoxes
        );

        PrimitivePlan moveBackPlan = new PrimitivePlan();

        // Find plan for moving out of the way
        PrimitivePlan outOfTheWayPlan = htnPlanner.plan();

        if (!conflict.getInitiatorPlan().getActions().isEmpty()) {
            // Construct HTNPlanner for moveBackAction
            htnPlanner = new HTNPlanner(
                    new PlanningLevelService(
                            BDIService.getInstance().getBDILevelService().getLevel()
                    ),
                    moveBackAction,
                    RelaxationMode.None
            );

            // Find plan for moving back to original position
            moveBackPlan = htnPlanner.plan();
        }


        return getResolvedConflict(parkingSpaces, outOfTheWayPlan, moveBackPlan, conflict);
    }

    public ResolvedConflict getResolvedConflictForAgentAndBox(Conflict conflict, List<Position> parkingSpaces, Box box){
        // If the conflicting action was push or pull, we must also move the box out of the way.

        //We will move the box to the first parking space (which is closest to the place where the conflict
        //happened). Then the agent will go in the second parking space, s.t. it shortens the path it has to
        //take in order to resolve the conflict

        //Make the plan for moving the box and the agent out of the way

        Position agentPositionBeforeConflict = BDIService.getInstance().getBDILevelService().
                getPosition(conflict.getInitiator().getLabel());
        Position boxPositionBeforeConflict = BDIService.getInstance().getBDILevelService().
                getPosition(box);

        PrimitivePlan moveBoxAndAgentOutOfTheWayPlan = getPlanToMoveBoxAndAgent(
                box,
                parkingSpaces.get(0),
                parkingSpaces.get(1)
        );

        PrimitivePlan moveBoxAndAgentBackPlan = getPlanToMoveBoxAndAgent(
               box,
                boxPositionBeforeConflict,
                agentPositionBeforeConflict
        );

        return getResolvedConflict(parkingSpaces, moveBoxAndAgentOutOfTheWayPlan, moveBoxAndAgentBackPlan, conflict);
    }

    public PrimitivePlan getPlanToMoveBoxAndAgent(Box box, Position boxDestination, Position agentDestination) {
        HMoveBoxAction moveBoxOutOfTheWayAction = new HMoveBoxAction(
                box,
                boxDestination,
                agentDestination
        );

        HTNPlanner htnPlanner = new HTNPlanner(
                new PlanningLevelService(
                        BDIService.getInstance().getBDILevelService().getLevel()
                ),
                moveBoxOutOfTheWayAction,
                RelaxationMode.None
        );

        return htnPlanner.plan();
    }

    public ResolvedConflict getResolvedConflict(List<Position> parkingSpaces,
                                                PrimitivePlan outOfTheWayPlan,
                                                PrimitivePlan moveBackPlan,
                                                Conflict conflict) {
        // Append NoOp actions after moving away
        // I don't think we have to wait that much. It's enough to wait until the conceder has moved past the parking space
        /*IntStream.range(0, outOfTheWayPlan.size()).forEach((number) ->
            outOfTheWayPlan.addAction(new NoConcreteAction())
        );*/

        IntStream.range(0, 2).forEach((number) ->
                outOfTheWayPlan.addAction(new NoConcreteAction())
        );

        // Append moveBackPlan
        outOfTheWayPlan.appendActions(moveBackPlan);

        // Initialize concederPlan
        PrimitivePlan concederPlan = new PrimitivePlan();

        // Add NoOp actions so that it waits until the initiator is away
        concederPlan.addAction(new NoConcreteAction());

        // Append the original concederPlan
        concederPlan.appendActions((PrimitivePlan) conflict.getConcederPlan());

        // Append the original plan
        outOfTheWayPlan.appendActions((PrimitivePlan) conflict.getInitiatorPlan());

        System.err.println("We have just solved the conflict between agents " + conflict.getInitiator().getLabel()
                + " and agent " + conflict.getConceder().getLabel());
        System.err.println("The plans for conlict resolution are: ");
        System.err.println(outOfTheWayPlan.getActions().toString());
        System.err.println(concederPlan.getActions().toString());

        // Return new resolved Conflict
        return new ResolvedConflict(
                conflict.getInitiator(),
                outOfTheWayPlan,
                conflict.getConceder(),
                concederPlan
        );
    }
}
