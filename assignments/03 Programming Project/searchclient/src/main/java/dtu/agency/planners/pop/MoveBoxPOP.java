package dtu.agency.planners.pop;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.MoveBoxAbstractAction;
import dtu.agency.actions.concreteaction.ActionComparator;
import dtu.agency.actions.concreteaction.MoveBoxConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.pop.preconditions.BoxAtPrecondition;

import java.util.PriorityQueue;
import java.util.Stack;

public class MoveBoxPOP extends AbstractPOP<MoveBoxAbstractAction> {

    private Position boxStartPosition;

    public MoveBoxPOP(Agent agent) {
        super(agent);
    }

    public POPPlan plan(MoveBoxAbstractAction action) {
        Stack<ConcreteAction> concreteActions = new Stack<>();

        /*
        Position goalPosition = GlobalLevelService.getInstance().getPosition(action.getAgentDestination().getLabel());

        Box box = action.getBox();
        boxStartPosition = GlobalLevelService.getInstance().getPosition(box.getLabel());

        Precondition currentPrecondition = new BoxAtPrecondition(box, agent, goalPosition);

        while (true) {

            PriorityQueue<MoveBoxConcreteAction> stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);
            MoveBoxConcreteAction nextAction = stepActions.poll();

            Position nextActionPosition = GlobalLevelService.getInstance().getPositionInDirection(
                    nextAction.getBoxPosition(),
                    nextAction.getBoxDirection()
            );

            if (nextActionPosition.isAdjacentTo(boxStartPosition)) {
                // Bigger switch please
                switch (nextAction.getType()) {
                    case PUSH:
                        concreteActions.add(new PushConcreteAction(
                                box,
                                agent,
                                boxStartPosition,
                                agentStartPosition,
                                GlobalLevelService.getInstance().getMovingDirection(
                                        boxStartPosition,
                                        nextActionPosition
                                ),
                                GlobalLevelService.getInstance().getMovingDirection(
                                        agentStartPosition,
                                        boxStartPosition
                                ),
                                Integer.MIN_VALUE
                        ));
                        break;
                    case PULL:
                        concreteActions.add(new PullConcreteAction(
                                box,
                                agent,
                                boxStartPosition,
                                agentStartPosition,
                                GlobalLevelService.getInstance().getMovingDirection(
                                        boxStartPosition,
                                        nextActionPosition
                                ),
                                GlobalLevelService.getInstance().getMovingDirection(
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

            concreteActions.add(nextAction);
            currentPrecondition = new BoxAtPrecondition(box, agent, nextAction.getBoxPosition());
        }
        */

        return new POPPlan(concreteActions);
    }

    /**
     * @param boxPrecondition
     * @return A queue of MoveBoxActions which solves the given precondition
     */
    public PriorityQueue<MoveBoxConcreteAction> solvePrecondition(BoxAtPrecondition boxPrecondition) {

        PriorityQueue<MoveBoxConcreteAction> actions = new PriorityQueue<>(new ActionComparator());

        /*

        // Find the free neighbour cells to the boxPrecondition
        List<Neighbour> freeBoxNeighbours = GlobalLevelService.getInstance().getFreeNeighbours(
                boxPrecondition.getBoxPosition()
        );

        if (freeBoxNeighbours.size() == 0) {
            // No free neighbours to the boxPrecondition, so we see if we can try moving a neighbour
            List<Neighbour> moveableBoxNeighbours = GlobalLevelService.getInstance().getMoveableNeighbours(
                    boxPrecondition.getBoxPosition()
            );

            // TODO do something with the moveable neighbours

            if (moveableBoxNeighbours.size() == 0) {
                // Nothing we can do at this point. Backtrack? Or fail outright?
            }

            for (Neighbour neighbour : moveableBoxNeighbours) {
                // Move the neighbours if possible
                if (neighbour.getPosition() == GlobalLevelService.getInstance().getPosition(boxPrecondition.getBox().getLabel())) {
                    // This neighbour is actually the box we are trying to move
                }
            }
        }

        for (Neighbour boxNeighbour : freeBoxNeighbours) {

            // Find the free cells where the agent can be
            List<Neighbour> viableAgentPositions = GlobalLevelService.getInstance().getFreeNeighbours(
                    boxNeighbour.getPosition()
            );

            for (Neighbour viableAgentNeighbour : viableAgentPositions) {

                // Push the box
                PushConcreteAction nextPushAction = new PushConcreteAction(
                        boxPrecondition.getBox(),
                        boxPrecondition.getAgent(),
                        boxNeighbour.getPosition(),
                        viableAgentNeighbour.getPosition(),
                        boxNeighbour.getDirection().getInverse(),
                        viableAgentNeighbour.getDirection().getInverse(),
                        GlobalLevelService.getInstance().manhattanDistance(
                                viableAgentNeighbour.getPosition(),
                                agentStartPosition
                        )
                );

                // Pull the box
                PullConcreteAction nextPullAction = new PullConcreteAction(
                        boxPrecondition.getBox(),
                        boxPrecondition.getAgent(),
                        boxNeighbour.getPosition(),
                        viableAgentNeighbour.getPosition(),
                        viableAgentNeighbour.getDirection(),
                        viableAgentNeighbour.getDirection().getInverse(),
                        GlobalLevelService.getInstance().manhattanDistance(
                                viableAgentNeighbour.getPosition(),
                                agentStartPosition
                        )
                );

                actions.add(nextPullAction);
                actions.add(nextPushAction);
            }
        }
        */

        return actions;
    }
}
