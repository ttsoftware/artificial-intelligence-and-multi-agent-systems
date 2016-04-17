package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.ActionComparator;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.planners.pop.preconditions.AgentAtPrecondition;
import dtu.agency.planners.pop.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Neighbour;
import dtu.agency.board.Position;
import dtu.agency.actions.abstractaction.GotoAbstractAction;
import dtu.agency.services.LevelService;
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

    public Stack<ConcreteAction> getPlan(Position currentAgentPosition, Stack<MoveConcreteAction> previousActions) {
        Stack<ConcreteAction> actions = new Stack<>();

        Precondition currentPrecondition = new AgentAtPrecondition(agent, currentAgentPosition);

        if (currentAgentPosition.isAdjacentTo(agentStartPosition)) {
            actions.add(new MoveConcreteAction(agent, currentAgentPosition, LevelService.getInstance()
                    .getMovingDirection(agentStartPosition, currentAgentPosition), 0));
            return actions;
        }

        PriorityQueue<ConcreteAction> stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

        boolean foundNextAction = false;

        while(!stepActions.isEmpty() && !foundNextAction) {
            MoveConcreteAction nextAction = (MoveConcreteAction) stepActions.poll();

            boolean foundCorrectAction = false;

            while (!foundCorrectAction) {
                if (introducesCycle(nextAction, previousActions)) {
                    if (!stepActions.isEmpty()) {
                        nextAction = (MoveConcreteAction) stepActions.poll();
                    } else {
                        return null;
                    }
                } else {
                    foundCorrectAction = true;
                }
            }

            previousActions.add(nextAction);

            if ((actions = getPlan(nextAction.getAgentPosition(), previousActions)) == null || actions.isEmpty()) {
                previousActions.remove(nextAction);
                if (stepActions.isEmpty()) {
                    return null;
                }
            } else {
                foundNextAction = true;
            }

            if (actions != null && actions.size() == 1) {
                actions.addAll(0, previousActions);
            }
        }

        return actions;
    }


    public boolean introducesCycle(MoveConcreteAction nextAction, Stack<MoveConcreteAction> previousActions)
    {
        for (MoveConcreteAction previousAction : previousActions) {
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
    public PriorityQueue<ConcreteAction> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<ConcreteAction> concreteActions = new PriorityQueue<>(new ActionComparator());

        List<Neighbour> neighbours = LevelService.getInstance().getFreeNeighbours(
                precondition.getAgentPreconditionPosition()
        );

        for (Neighbour neighbour : neighbours) {
            MoveConcreteAction nextAction = new MoveConcreteAction(
                    agent,
                    neighbour.getPosition(),
                    neighbour.getDirection().getInverse(),
                    LevelService.getInstance().manhattanDistance(neighbour.getPosition(), agentStartPosition)
            );
            concreteActions.add(nextAction);
        }

        return concreteActions;
    }
}
