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

import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class GotoPOP extends AbstractPOP<GotoAction> {

    public GotoPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(GotoAction action) {

        Stack<Action> actions = new Stack<>();

        Precondition currentPrecondition = new AgentAtPrecondition(agent, action.getPosition());

        while (true) {
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
            currentPrecondition = new AgentAtPrecondition(agent, nextAction.getAgentPosition());
        }

        return new POPPlan(actions);
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
