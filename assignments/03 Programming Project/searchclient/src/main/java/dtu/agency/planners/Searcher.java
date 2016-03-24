package dtu.agency.planners;

import dtu.agency.BoardObjectHelper;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.*;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.planners.actions.preconditions.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Searcher {

    private Level level;
    private BoardObjectHelper boardObjectHelper;

    public Searcher(Level level) {
        this.level = level;
        this.boardObjectHelper = new BoardObjectHelper(level.getBoardObjects(), level.getBoardState());
    }

//    public List<Action> search(AbstractAction action, Agent agent) {
//        List<Action> actions = new ArrayList<>();
//        Position agentPosition = level.getBoardObjectPositions().get(agent.getLabel());
//
//        List<List<Action>> allOptimalActions = new ArrayList<>();
//        while(!boardObjectHelper.isAdjacent(agentPosition, action.getPosition())) {
//            List<Pair<BoardObject, Position>> neighbours = boardObjectHelper.getFreeNeighbours(agentPosition);
//
//            int min_h = 32000;
//            List<Action> optimalActions = new ArrayList<>();
//            for(Pair<BoardObject, Position> neighbour : neighbours) {
//                int h = heuristic(neighbour.getValue(), action.getPosition());
//                if (h < min_h) {
//                    min_h = h;
//                    optimalActions = new ArrayList<>();
//
//                    Direction movingDirection = boardObjectHelper.getMovingDirection(agentPosition, neighbour.getValue());
//
//                    if (neighbour.getClass().getName().equals("Box")) { }
//                    else {
//
//                    }
//
//                    MoveAction a = new MoveAction(boardObjectHelper.getMovingDirection(agentPosition, neighbour.getValue()));
//                    optimalActions.add(a);
//                }
//                else if (h == min_h){
//                    MoveAction a = new MoveAction(boardObjectHelper.getMovingDirection(agentPosition, neighbour.getValue()));
//                    optimalActions.add(a);
//                }
//            }
//
//            actions.add(optimalActions.get(0));
//        }
//
//        return actions;
//    }

    public List<Action> search(AbstractAction action, Agent agent) {
        List<Action> actions = new ArrayList<>();
        List<Precondition> preconditions = new ArrayList<>();
        Position agentPosition = level.getBoardObjectPositions().get(agent.getLabel());
        Position goalPosition = action.getPosition();

        switch (action.getClass().getName()) {
            case "GoToAction":
                preconditions.add(new AgentAtPrecondition(agent, goalPosition));
                break;
            case "MoveBoxAction":
                preconditions.add(new BoxAtPrecondition(((MoveBoxAction)action).getBox(), goalPosition));
                break;
        }

        List<Precondition> openPreconditions = getOpenPreconditions(preconditions);

        while (openPreconditions.size() != 0) {
            Precondition currentPrecondition = openPreconditions.remove(0);

            PriorityQueue<Action> stepActions = new PriorityQueue<>();
            switch (currentPrecondition.getClass().getName()) {
                case "FreeCellPrecondition":
                    stepActions = solvePrecondition((FreeCellPrecondition) currentPrecondition);
                    break;
                case "AgentAtPrecondition":
                    stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);
                    break;
                case "NeighbourPrecondition":
                    break;
                default:
                    break;
            }

            actions.add(stepActions.remove());
        }

        return actions;
    }

    public PriorityQueue<Action> solvePrecondition(FreeCellPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>();

        List<Pair<Position, Direction>> neighbours = boardObjectHelper.getFreeNeighbours(precondition.getPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
            actions.add(new MoveAction(neighbour.getValue()));
        }

        return actions;
    }

    public PriorityQueue<Action> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>();

        List<Pair<Position, Direction>> neighbours = boardObjectHelper.getFreeNeighbours(precondition.getPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
            actions.add(new MoveAction(neighbour.getValue()));
        }

        return actions;
    }

    public PriorityQueue<Action> solvePrecondition(NeighbourPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>();

        List<Pair<Position, Direction>> neighbours = boardObjectHelper.getFreeNeighbours(precondition.getPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
            actions.add(new MoveAction(neighbour.getValue()));
        }

        return actions;
    }

    public PriorityQueue<Action> solvePrecondition(BoxAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>();

        List<Pair<Position, Direction>> neighbours = boardObjectHelper.getFreeNeighbours(precondition.getPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
//            Direction agentDirection = boardObjectHelper.getMovingDirection(precondition.get)
//            actions.add(new PushAction(precondition.getBox(), ));
        }

        return actions;
    }

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
                case "NeighbourPrecondition":
                    if (isOpenPrecondition((NeighbourPrecondition) precondition)) {
                        openPreconditions.add(precondition);
                    }
                    else {
                        precondition.setSatisfied(true);
                    }
                    break;
                default:
                    return null;
            }
        }

        return openPreconditions;
    }

    public boolean isOpenPrecondition(FreeCellPrecondition precondition) {
        Position position = precondition.getPosition();
        return !level.getBoardState()[position.getRow()][position.getColumn()].equals(BoardCell.FREE_CELL);
    }

    public boolean isOpenPrecondition(AgentAtPrecondition precondition) {
        Position position = precondition.getPosition();
        Position objectPosition = level.getBoardObjectPositions().get(precondition.getAgent().getLabel());
        return objectPosition.equals(position);
    }

    public boolean isOpenPrecondition(BoxAtPrecondition precondition) {
        Position position = precondition.getPosition();
        Position objectPosition = level.getBoardObjectPositions().get(precondition.getBox().getLabel());
        return objectPosition.equals(position);
    }

    public boolean isOpenPrecondition(NeighbourPrecondition precondition) {
        Position position = precondition.getPosition();
        Position objectPosition = level.getBoardObjectPositions().get(precondition.getObject().getLabel());
        return boardObjectHelper.isAdjacent(objectPosition, position);
    }

    private int heuristic(Position agentPosition, Position goalPosition) {

        return Math.abs(agentPosition.getRow() - goalPosition.getRow()) + Math.abs(agentPosition.getColumn() - goalPosition.getColumn());
    }
}
