package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.ActionComparator;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.preconditions.AgentAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.services.LevelService;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GotoPOP extends AbstractPOP<GotoAction> {

    public GotoPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(GotoAction action) {
        List<Action> actions = new ArrayList<>();
        List<Precondition> preconditions = new ArrayList<>();
        Position goalPosition = action.getPosition();

        preconditions.add(new AgentAtPrecondition(agent, goalPosition));

        List<Precondition> openPreconditions = getOpenPreconditions(preconditions);

        while (openPreconditions.size() != 0) {
            Precondition currentPrecondition = openPreconditions.remove(0);

            PriorityQueue<Action> stepActions = new PriorityQueue(new ActionComparator());
            stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

            Action nextAction = stepActions.poll();
            actions.add(nextAction);
            openPreconditions.addAll(nextAction.getPreconditions());
            openPreconditions = getOpenPreconditions(openPreconditions);
        }

        return new POPPlan(actions);
    }

    public PriorityQueue<Action> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator());

        List<Pair<Position, Direction>> neighbours = boardObjectService.getFreeNeighbours(precondition.getAgentPosition());

        for (Pair<Position, Direction> neighbour : neighbours) {
            MoveAction nextAction = new MoveAction(neighbour.getValue());
            nextAction.setHeuristic(heuristic(neighbour.getKey(), this.agentStartPosition));
            actions.add(nextAction);
        }

        return actions;
    }

    public List<Precondition> getOpenPreconditions(List<Precondition> preconditions) {
        List<Precondition> openPreconditions = new ArrayList<>();
        for (Precondition precondition : preconditions) {
            if (isOpenPrecondition((AgentAtPrecondition) precondition)) {
                openPreconditions.add(precondition);
            } else {
                precondition.setSatisfied(true);
            }
        }
        return openPreconditions;
    }

    public boolean isOpenPrecondition(AgentAtPrecondition precondition) {
        Position position = precondition.getAgentPosition();
        Position objectPosition = LevelService.getInstance().getPosition(precondition.getAgent().getLabel());
        return objectPosition.equals(position);
    }
}
