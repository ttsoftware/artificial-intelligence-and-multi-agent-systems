package dtu.agency.planners.pop;

import dtu.agency.agent.actions.*;
import dtu.agency.agent.actions.preconditions.BoxAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Neighbour;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.MoveBoxAbstractAction;
import dtu.agency.services.LevelService;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class MoveBoxPOP extends AbstractPOP<MoveBoxAbstractAction> {

    private Position boxStartPosition;

    public MoveBoxPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(MoveBoxAbstractAction action) {
        Stack<Action> actions = new Stack<>();

        Position goalPosition = LevelService.getInstance().getPosition(action.getGoal().getLabel());

        Box box = action.getBox();
        boxStartPosition = LevelService.getInstance().getPosition(box.getLabel());

        Precondition currentPrecondition = new BoxAtPrecondition(box, agent, goalPosition);

        while (true) {

            PriorityQueue<MoveBoxAction> stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);
            MoveBoxAction nextAction = stepActions.poll();

            Position nextActionPosition = LevelService.getInstance().getPositionInDirection(
                    nextAction.getBoxPosition(),
                    nextAction.getBoxDirection()
            );

            if (nextActionPosition.isAdjacentTo(boxStartPosition)) {
                // Bigger switch please
                switch (nextAction.getType()) {
                    case PUSH:
                        actions.add(new PushAction(
                                box,
                                agent,
                                boxStartPosition,
                                agentStartPosition,
                                LevelService.getInstance().getMovingDirection(
                                        boxStartPosition,
                                        nextActionPosition
                                ),
                                LevelService.getInstance().getMovingDirection(
                                        agentStartPosition,
                                        boxStartPosition
                                ),
                                Integer.MIN_VALUE
                        ));
                        break;
                    case PULL:
                        actions.add(new PullAction(
                                box,
                                agent,
                                boxStartPosition,
                                agentStartPosition,
                                LevelService.getInstance().getMovingDirection(
                                        boxStartPosition,
                                        nextActionPosition
                                ),
                                LevelService.getInstance().getMovingDirection(
                                        agentStartPosition,
                                        nextActionPosition
                                ),
                                Integer.MIN_VALUE
                        ));
                        break;
                    default:
                        throw new UnsupportedOperationException("Something is wrong and you should feel wrong.");
                }
                break;
            }

            actions.add(nextAction);
            currentPrecondition = new BoxAtPrecondition(box, agent, nextAction.getBoxPosition());
        }

        return new POPPlan(actions);
    }

    /**
     *
     * @param boxPrecondition
     * @return A queue of MoveBoxActions which solves the given precondition
     */
    public PriorityQueue<MoveBoxAction> solvePrecondition(BoxAtPrecondition boxPrecondition) {

        PriorityQueue<MoveBoxAction> actions = new PriorityQueue<>(new ActionComparator());

        // Find the free neighbour cells to the boxPrecondition
        List<Neighbour> boxNeighbours = LevelService.getInstance().getFreeNeighbours(
                boxPrecondition.getBoxPosition()
        );

        for (Neighbour boxNeighbour : boxNeighbours) {

            // Find the free cells where the agent can be
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(
                    boxNeighbour.getPosition()
            );

            for (Neighbour viableAgentPosition : viableAgentPositions) {

                // Push the box
                PushAction nextPushAction = new PushAction(
                        boxPrecondition.getBox(),
                        boxPrecondition.getAgent(),
                        boxNeighbour.getPosition(),
                        viableAgentPosition.getPosition(),
                        boxNeighbour.getDirection().getInverse(),
                        viableAgentPosition.getDirection().getInverse(),
                        LevelService.getInstance().manhattanDistance(
                                viableAgentPosition.getPosition(),
                                agentStartPosition
                        )
                );
                actions.add(nextPushAction);
            }
        }

        for (Neighbour boxNeighbour : boxNeighbours) {

            // Find free cells where the agent can be
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(
                    boxNeighbour.getPosition()
            );

            for (Neighbour viableAgentPosition : viableAgentPositions) {

                // Pull the box
                PullAction nextPullAction = new PullAction(
                        boxPrecondition.getBox(),
                        boxPrecondition.getAgent(),
                        boxNeighbour.getPosition(),
                        viableAgentPosition.getPosition(),
                        viableAgentPosition.getDirection(),
                        viableAgentPosition.getDirection().getInverse(),
                        LevelService.getInstance().manhattanDistance(
                                viableAgentPosition.getPosition(),
                                agentStartPosition
                        )
                );
                actions.add(nextPullAction);
            }
        }

        return actions;
    }
}
