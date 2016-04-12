package dtu.agency.planners.pop;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.ActionComparator;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.MoveBoxAction;
import dtu.agency.agent.actions.preconditions.AgentAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Neighbour;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.GotoAbstractAction;
import dtu.agency.planners.actions.MoveBoxAbstractAction;
import dtu.agency.services.LevelService;
import server.action.Move;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class GotoPOP extends AbstractPOP<GotoAbstractAction> {

    private Position agentStartPosition;

    public GotoPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(GotoAbstractAction gotoAbstractAction)
    {
        Position goalPosition = gotoAbstractAction.getPosition();
        agentStartPosition = LevelService.getInstance().getPosition(agent.getLabel());

        return new POPPlan(getPlan(goalPosition, new Stack<>()));
    }

    public Stack<Action> getPlan(Position currentAgentPosition, Stack<MoveAction> previousActions) {
        Stack<Action> actions = new Stack<>();

        Precondition currentPrecondition = new AgentAtPrecondition(agent, currentAgentPosition);

        if (currentAgentPosition.isAdjacentTo(agentStartPosition)) {
            actions.add(new MoveAction(agent, currentAgentPosition, LevelService.getInstance()
                    .getMovingDirection(agentStartPosition, currentAgentPosition), 0));
            return actions;
        }

        PriorityQueue<Action> stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

        if (!stepActions.isEmpty()) {
            MoveAction nextAction = (MoveAction) stepActions.poll();

            boolean foundCorrectAction = false;

            while (!foundCorrectAction) {
                if (introducesCycle(nextAction, previousActions)) {
                    if (!stepActions.isEmpty()) {
                        nextAction = (MoveAction) stepActions.poll();
                    } else {
                        return null;
                    }
                } else {
                    foundCorrectAction = true;
                }
            }

            previousActions.add(nextAction);

            while ((actions = getPlan(nextAction.getAgentPosition(), previousActions)) == null) {
                if (!stepActions.isEmpty()) {
                    previousActions.remove(nextAction);
                    nextAction = (MoveAction) stepActions.poll();
                } else {
                    return null;
                }
            }

            if (actions.size() == 1) {
                actions.addAll(0, previousActions);
            }
        }

        return actions;
    }


    public boolean introducesCycle(MoveAction nextAction, Stack<MoveAction> previousActions)
    {
        for (MoveAction previousAction : previousActions) {
            if (nextAction.getAgentPosition().equals(previousAction.getAgentPosition())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param precondition
     * @return A queue of MoveActions which solves the given precondition
     */
    public PriorityQueue<Action> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<Action> actions = new PriorityQueue<>(new ActionComparator());

        List<Neighbour> neighbours = LevelService.getInstance().getFreeNeighbours(
                precondition.getAgentPreconditionPosition()
        );

        for (Neighbour neighbour : neighbours) {
            MoveAction nextAction = new MoveAction(
                    agent,
                    neighbour.getPosition(),
                    neighbour.getDirection().getInverse(),
                    LevelService.getInstance().manhattanDistance(neighbour.getPosition(), agentStartPosition)
            );
            actions.add(nextAction);
        }

        return actions;
    }
}