package dtu.agency.planners.pop;

import dtu.agency.services.BoardObjectService;
import dtu.agency.agent.actions.*;
import dtu.agency.board.*;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.agent.actions.preconditions.*;
import dtu.agency.services.LevelService;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GotoPOP extends AbstractPOP {


    public List<Action> search(AbstractAction action) {
        List<Action> actions = new ArrayList<>();
        List<Precondition> preconditions = new ArrayList<>();
        Position goalPosition = action.getPosition();

        switch (action.getClass().getName()) {
            case "GoToAction":
                preconditions.add(new AgentAtPrecondition(agent, goalPosition));
                break;
            case "MoveBoxAction":
                Box box = ((MoveBoxAction)action).getBox();
                this.boxStartPosition = level.getBoardObjectPositions().get(box.getLabel());
                preconditions.add(new BoxAtPrecondition(box, agent, goalPosition));

//                List<Pair<Position, Direction>> freeNeighbours = boardObjectService.getFreeNeighbours(goalPosition);
//                for (Pair<Position, Direction> freeNeighbour : freeNeighbours) {
//                    // Maybe choose the one with the closest distance to the agent, so that
//                    // we minimize the chance of picking an agent position that renders the level
//                    // unsolvable.
//                    preconditions.add(new BoxAtAndAgentAtPrecondition(boxAtPrecondition,
//                            new AgentAtPrecondition(agent, freeNeighbour.getKey())));
//                }

                break;
        }

        List<Precondition> openPreconditions = getOpenPreconditions(preconditions);

        while (openPreconditions.size() != 0) {
            Precondition currentPrecondition = openPreconditions.remove(0);

            PriorityQueue<Action> stepActions = new PriorityQueue<>(new ActionComparator());
            switch (currentPrecondition.getClass().getName()) {
//                case "FreeCellPrecondition":
//                    stepActions = solvePrecondition((FreeCellPrecondition) currentPrecondition);
//                    break;
                case "AgentAtPrecondition":
                    stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);
                    break;
                case "BoxAtPrecondition":
                    // If we have a BoxAtPrecondition, we also have an AgentAtPrecondition following it
                    // which we must solve simultaneously
//                    Precondition nextPrecondition = openPreconditions.remove(0);
//                    stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition, (AgentAtPrecondition) nextPrecondition);
                    stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);
                    break;
//                case "NeighbourPrecondition":
//                    stepActions = solvePrecondition((NeighbourPrecondition) currentPrecondition);
//                    break;
                default:
                    break;
            }

            Action nextAction = stepActions.remove();
            actions.add(nextAction);
            openPreconditions.addAll(nextAction.getPreconditions());
            openPreconditions = getOpenPreconditions(openPreconditions);
        }

        return actions;
    }

//    public PriorityQueue<Action> solvePrecondition(FreeCellPrecondition precondition) {
//        PriorityQueue<Action> actions = new PriorityQueue<>();
//
//        List<Pair<Position, Direction>> neighbours = boardObjectService.getFreeNeighbours(precondition.getPosition());
//
//        for (Pair<Position, Direction> neighbour : neighbours) {
//            MoveAction nextAction = new MoveAction(neighbour.getValue());
//            nextAction.setHeuristic(heuristic(neighbour.getKey(), this.agentStartPosition));
//            actions.add(nextAction);
//        }
//
//        return actions;
//    }

    public PriorityQueue<Action> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator<>());

        List<Pair<Position, Direction>> neighbours = boardObjectService.getFreeNeighbours(precondition.getAgentPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
            MoveAction nextAction = new MoveAction(neighbour.getValue());
            nextAction.setHeuristic(heuristic(neighbour.getKey(), this.agentStartPosition));
            actions.add(nextAction);
        }

        return actions;
    }

    public PriorityQueue<Action> solvePrecondition(BoxAtPrecondition boxPrecondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator<>());
        List<Pair<Position, Direction>> boxNeighbours = boardObjectService.getFreeNeighbours(boxPrecondition.getBoxPosition());

        for (Pair<Position, Direction> boxNeighbour : boxNeighbours) {
            List<Pair<Position, Direction>> viableAgentPositions = boardObjectService.getFreeNeighbours(boxNeighbour.getKey());
            for (Pair<Position, Direction> viableAgentPosition : viableAgentPositions) {

                PushAction nextPushAction = new PushAction(boxPrecondition.getBox(), boxNeighbour.getKey(),
                        boxPrecondition.getAgent(), viableAgentPosition.getKey(),
                        viableAgentPosition.getValue().getInverse(), boxNeighbour.getValue().getInverse());
                nextPushAction.setHeuristic(heuristic(viableAgentPosition.getKey(), this.agentStartPosition));
                actions.add(nextPushAction);
            }
        }

        for (Pair<Position, Direction> boxNeighbour : boxNeighbours) {
            List<Pair<Position, Direction>> viableAgentPositions = boardObjectService.getFreeNeighbours(boxNeighbour.getKey());
            for (Pair<Position, Direction> viableAgentPosition : viableAgentPositions) {

                PullAction nextPullAction = new PullAction(boxPrecondition.getBox(), boxNeighbour.getKey(),
                        boxPrecondition.getAgent(), viableAgentPosition.getKey(),
                        viableAgentPosition.getValue().getInverse(), viableAgentPosition.getValue());
                nextPullAction.setHeuristic(heuristic(viableAgentPosition.getKey(), this.agentStartPosition));
                actions.add(nextPullAction);
            }
        }

        return actions;
    }

//    public PriorityQueue<Action> solvePrecondition(BoxAtPrecondition boxPrecondition, AgentAtPrecondition agentPrecondition) {
//        PriorityQueue<Action> actions = new PriorityQueue<>();
//
//        List<Pair<Position, Direction>> agentNeighbours = boardObjectService.getFreeNeighbours(agentPrecondition.getAgentPosition());
//        for (Pair<Position, Direction> agentNeighbour : agentNeighbours) {
//            Direction agentMovingDirection = boardObjectService.getMovingDirection(agentNeighbour.getKey(),
//                    agentPrecondition.getAgentPosition());
//            Direction boxMovingDirection = boardObjectService.getMovingDirection(agentPrecondition.getAgentPosition(),
//                    boxPrecondition.getBoxPosition());
//
//            PushAction nextPushAction = new PushAction(boxPrecondition.getBox(), agentPrecondition.getAgentPosition(),
//                    agentPrecondition.getAgent(), agentNeighbour.getKey(), agentMovingDirection, boxMovingDirection);
//            nextPushAction.setHeuristic(heuristic(boxPrecondition.getBoxPosition(), this.boxStartPosition));
//            actions.add(nextPushAction);
//        }
//
//
//        List<Pair<Position, Direction>> boxNeighbours = boardObjectService.getFreeNeighbours(boxPrecondition.getBoxPosition());
//        for (Pair<Position, Direction> boxNeighbour : boxNeighbours) {
//            Direction agentMovingDirection = boardObjectService.getMovingDirection(boxPrecondition.getBoxPosition(),
//                agentPrecondition.getAgentPosition());
//            Direction boxMovingDirection = boardObjectService.getMovingDirection(boxNeighbour.getKey(),
//                boxPrecondition.getBoxPosition());
//
//            PullAction nextPullAction = new PullAction(boxPrecondition.getBox(), boxNeighbour.getKey(),
//                    agentPrecondition.getAgent(), boxPrecondition.getBoxPosition(), agentMovingDirection, boxMovingDirection);
//            nextPullAction.setHeuristic(heuristic(boxPrecondition.getBoxPosition(), this.boxStartPosition));
//            actions.add(nextPullAction);
//        }
//
//        return actions;
//    }

//    public PriorityQueue<Action> solvePrecondition(NeighbourPrecondition precondition) {
//        PriorityQueue<Action> actions = new PriorityQueue<>();
//
//        if (precondition.getDirection() == null) {
//
//        }
//
//        List<Pair<Position, Direction>> neighbours = boardObjectService.getFreeNeighbours(precondition.getAgentPosition());
//
//        for (Pair<Position, Direction> neighbour : neighbours) {
//            actions.add(new MoveAction(neighbour.getValue()));
//        }
//
//        return actions;
//    }

    public List<Precondition> getOpenPreconditions(List<Precondition> preconditions) {
        List<Precondition> openPreconditions = new ArrayList<>();
        for (Precondition precondition : preconditions) {
            switch (precondition.getClass().getName()) {
                case "FreeCellPrecondition":
                    if (isOpenPrecondition((FreeCellPrecondition) precondition)) {
                        openPreconditions.add(precondition);
                    }
                    else {
                        precondition.setSatisfied(true);
                    }
                    break;
                case "AgentAtPrecondition":
                    if (isOpenPrecondition((AgentAtPrecondition) precondition)) {
                        openPreconditions.add(precondition);
                    }
                    else {
                        precondition.setSatisfied(true);
                    }
                    break;
                case "BoxAtPrecondition":
                    if (isOpenPrecondition((BoxAtPrecondition) precondition)) {
                        openPreconditions.add(precondition);
                    }
                    else {
                        precondition.setSatisfied(true);
                    }
                    break;
//                case "NeighbourPrecondition":
//                    if (isOpenPrecondition((NeighbourPrecondition) precondition)) {
//                        openPreconditions.add(precondition);
//                    }
//                    else {
//                        precondition.setSatisfied(true);
//                    }
//                    break;
                default:
                    return null;
            }
        }

        return openPreconditions;
    }

    public boolean isOpenPrecondition(FreeCellPrecondition precondition) {
        Position position = precondition.getPosition();
        return !LevelService.getInstance().getLevel().getBoardState()[position.getRow()][position.getColumn()].equals(BoardCell.FREE_CELL);
    }

    public boolean isOpenPrecondition(AgentAtPrecondition precondition) {
        Position position = precondition.getAgentPosition();
        Position objectPosition = LevelService.getInstance().getPosition(precondition.getAgent().getLabel());
        return objectPosition.equals(position);
    }

    public boolean isOpenPrecondition(BoxAtPrecondition precondition) {
        Position position = precondition.getBoxPosition();
        Position objectPosition = LevelService.getInstance().getPosition(precondition.getBox().getLabel());
        return objectPosition.equals(position);
    }

    public boolean isOpenPrecondition(NeighbourPrecondition precondition) {
        Position position = precondition.getPosition();
        Position objectPosition = LevelService.getInstance().getPosition(precondition.getObject().getLabel());
        return boardObjectService.isAdjacent(objectPosition, position);
    }
}