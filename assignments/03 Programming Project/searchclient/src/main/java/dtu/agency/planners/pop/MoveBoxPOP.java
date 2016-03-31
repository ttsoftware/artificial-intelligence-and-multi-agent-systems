package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.ActionComparator;
import dtu.agency.agent.actions.PullAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.agent.actions.preconditions.BoxAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Neighbour;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class MoveBoxPOP extends AbstractPOP<MoveBoxAction> {

    private Position boxStartPosition;

    public MoveBoxPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(MoveBoxAction action) {
        Stack<Action> actions = new Stack<>();
        /*
        List<Precondition> preconditions = new ArrayList<>();
        Position goalPosition = LevelService.getInstance().getPosition(action.getGoal().getLabel());

        Box box = action.getBox();
        boxStartPosition = LevelService.getInstance().getPosition(box.getLabel());
        preconditions.add(new BoxAtPrecondition(box, agent, goalPosition));

        List<Precondition> openPreconditions = getOpenPreconditions(preconditions);

        while (openPreconditions.size() != 0) {
            Precondition currentPrecondition = openPreconditions.remove(0);

            PriorityQueue<Action> stepActions = new PriorityQueue<>(new ActionComparator());

            stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);

            Action nextAction = stepActions.remove();
            actions.add(nextAction);
            openPreconditions.addAll(nextAction.findPreconditions());
            openPreconditions = getOpenPreconditions(openPreconditions);
        }
        */

        return new POPPlan(actions);
    }

    public PriorityQueue<Action> solvePrecondition(BoxAtPrecondition boxPrecondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator());
        List<Neighbour> boxNeighbours = LevelService.getInstance().getFreeNeighbours(boxPrecondition.getBoxPosition());

        for (Neighbour boxNeighbour : boxNeighbours) {
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(boxNeighbour.getPosition());
            for (Neighbour viableAgentPosition : viableAgentPositions) {

                PushAction nextPushAction = new PushAction(
                        boxPrecondition.getBox(),
                        boxNeighbour.getPosition(),
                        boxPrecondition.getAgent(),
                        viableAgentPosition.getPosition(),
                        viableAgentPosition.getDirection().getInverse(),
                        boxNeighbour.getDirection().getInverse(),
                        LevelService.getInstance().manhattanDistance(viableAgentPosition.getPosition(), agentStartPosition)
                );
                actions.add(nextPushAction);
            }
        }

        for (Neighbour boxNeighbour : boxNeighbours) {
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(boxNeighbour.getPosition());
            for (Neighbour viableAgentPosition : viableAgentPositions) {

                PullAction nextPullAction = new PullAction(
                        boxPrecondition.getBox(),
                        boxNeighbour.getPosition(),
                        boxPrecondition.getAgent(),
                        viableAgentPosition.getPosition(),
                        viableAgentPosition.getDirection().getInverse(),
                        viableAgentPosition.getDirection(),
                        LevelService.getInstance().manhattanDistance(viableAgentPosition.getPosition(), agentStartPosition)
                );
                actions.add(nextPullAction);
            }
        }

        return actions;
    }

    public List<Precondition> getOpenPreconditions(List<Precondition> preconditions) {
        List<Precondition> openPreconditions = new ArrayList<>();
        for (Precondition precondition : preconditions) {
            if (isOpenPrecondition((BoxAtPrecondition) precondition)) {
                openPreconditions.add(precondition);
            } else {
                precondition.setSatisfied(true);
            }
        }

        return openPreconditions;
    }

    public boolean isOpenPrecondition(BoxAtPrecondition precondition) {
        Position position = precondition.getBoxPosition();
        Position objectPosition = LevelService.getInstance().getPosition(precondition.getBox().getLabel());
        return objectPosition.equals(position);
    }
}
