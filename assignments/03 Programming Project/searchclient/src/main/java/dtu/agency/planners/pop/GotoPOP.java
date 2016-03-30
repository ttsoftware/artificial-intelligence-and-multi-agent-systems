package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.ActionComparator;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.preconditions.AgentAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Neighbour;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class GotoPOP extends AbstractPOP<GotoAction> {

    public GotoPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(GotoAction action) {

        Stack<Action> actions = new Stack<>();
        List<Precondition> preconditions = new ArrayList<>();

        Position objectivePosition = action.getPosition();

        preconditions.add(new AgentAtPrecondition(agent, objectivePosition));

        List<Precondition> openPreconditions = getOpenPreconditions(preconditions);

        while (openPreconditions.size() != 0) {
            Precondition currentPrecondition = openPreconditions.remove(0);

            PriorityQueue<Action> stepActions = new PriorityQueue(new ActionComparator());
            stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

            MoveAction nextAction = (MoveAction) stepActions.poll();

            Position nextActionPosition = LevelService.getInstance().getPositionInDirection(
                    nextAction.getAgentPosition(),
                    nextAction.getDirection()
            );

            if (nextActionPosition.isAdjacentTo(agentStartPosition)) {
                actions.add(
                        new MoveAction(
                                LevelService.getInstance().getMovingDirection(
                                        nextActionPosition,
                                        agentStartPosition
                                )
                        )
                );
                break;
            }

            actions.add(nextAction);

            openPreconditions.addAll(nextAction.findPreconditions());
            openPreconditions = getOpenPreconditions(openPreconditions);
        }

        return new POPPlan(actions);
    }

    public PriorityQueue<Action> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator());

        List<Neighbour> neighbours = LevelService.getInstance().getFreeNeighbours(
                precondition.getAgentPreconditionPosition()
        );

        for (Neighbour neighbour : neighbours) {
            MoveAction nextAction = new MoveAction(
                    neighbour.getDirection().getInverse(),
                    agent,
                    neighbour.getPosition()
            );
            nextAction.setHeuristic(
                    LevelService.getInstance().manhattanDistance(neighbour.getPosition(), agentStartPosition)
            );
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
        Position agentPreconditionPosition = precondition.getAgentPreconditionPosition();
        Position agentActualPosition = LevelService.getInstance().getPosition(precondition.getAgent().getLabel());
        return !agentActualPosition.equals(agentPreconditionPosition);
    }
}
