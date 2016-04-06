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

    public GotoPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(GotoAbstractAction action) {

        Stack<ConcreteAction> concreteActions = new Stack<>();
        Precondition currentPrecondition = new AgentAtPrecondition(agent, action.getPosition());

        while (true) {
            PriorityQueue<ConcreteAction> stepConcreteActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);
            MoveConcreteAction nextAction = (MoveConcreteAction) stepConcreteActions.poll();

            Position nextActionPosition = LevelService.getInstance().getAdjacentPositionInDirection(
                    nextAction.getAgentPosition(),
                    nextAction.getDirection()
            );

            if (nextActionPosition.isAdjacentTo(agentStartPosition)) {
                concreteActions.add(
                        new MoveConcreteAction(
                                LevelService.getInstance().getRelativeDirection(
                                        nextActionPosition,
                                        agentStartPosition,
                                        true
                                )
                        )
                );
                break;
            }

            concreteActions.add(nextAction);
            currentPrecondition = new AgentAtPrecondition(agent, nextAction.getAgentPosition());
        }

        if (!LevelService.getInstance().isFree(action.getPosition())) {
            // If the cell we are moving to is not free, we remove the last MoveConcreteAction
            concreteActions.remove(concreteActions.firstElement());
            return new POPPlan(concreteActions);
        }

        return new POPPlan(concreteActions);
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
