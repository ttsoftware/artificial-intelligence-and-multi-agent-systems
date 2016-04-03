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
        Position currentBoxPosition = goalPosition;
        Position currentAgentPosition = null;

        while (true) {

            if(currentBoxPosition.isAdjacentTo(boxStartPosition))
            {
                if(!currentBoxPosition.equals(agentStartPosition))
                {
                    actions.add(new PushAction(
                            box,
                            agent,
                            boxStartPosition,
                            agentStartPosition,
                            LevelService.getInstance().getMovingDirection(
                                    boxStartPosition,
                                    currentBoxPosition
                            ),
                            LevelService.getInstance().getMovingDirection(
                                    agentStartPosition,
                                    boxStartPosition
                            ),
                            Integer.MIN_VALUE
                    ));
                }
                else
                {
                    if(currentAgentPosition == null)
                    {
                        currentAgentPosition = LevelService.getInstance().
                                getFreeNeighbours(currentBoxPosition).get(0).getPosition();
                    }
                    actions.add(new PullAction(
                            box,
                            agent,
                            boxStartPosition,
                            agentStartPosition,
                            LevelService.getInstance().getMovingDirection(
                                    boxStartPosition,
                                    currentBoxPosition
                            ),
                            LevelService.getInstance().getMovingDirection(
                                    currentBoxPosition,
                                    currentAgentPosition
                            ),
                            Integer.MIN_VALUE
                    ));
                }
                break;
            }

            PriorityQueue<MoveBoxAction> stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);
            MoveBoxAction nextAction = stepActions.poll();

            if(nextAction != null)
            {
                currentBoxPosition = nextAction.getBoxPosition();
                currentAgentPosition = nextAction.getAgentPosition();

                actions.add(nextAction);
                currentPrecondition = new BoxAtPrecondition(box, agent, currentBoxPosition);
            }
            else
            {
                //TODO: backtrack
            }

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

        // Find the free neighbour cells to the box
        List<Neighbour> viableBoxPositions = LevelService.getInstance().getFreeNeighbours(
                boxPrecondition.getBoxPosition(), agent
        );

        if (viableBoxPositions.size() == 0) {
            // No free neighbours to the boxPrecondition
            List<Neighbour> moveableBoxNeighbours = LevelService.getInstance().getMoveableNeighbours(
                    boxPrecondition.getBoxPosition()
            );

            /*for(Neighbour movableNeighbour: moveableBoxNeighbours)
            {
                if(movableNeighbour.getPosition().equals(LevelService.getInstance().
                        getPosition(boxPrecondition.getBox().getLabel())))
                {
                    freeBoxNeighbours.add(movableNeighbour);
                    break;
                }
            }*/

            // TODO do something with the other moveable neighbours (apart from the box itself)
        }

        actions.addAll(getPushActions(viableBoxPositions, boxPrecondition));
        actions.addAll(getPullActions(viableBoxPositions, boxPrecondition));

        return actions;
    }

    public PriorityQueue<MoveBoxAction> getPushActions(List<Neighbour> viableBoxPositions, BoxAtPrecondition boxPrecondition)
    {
        PriorityQueue<MoveBoxAction> pushActions = new PriorityQueue<>(new ActionComparator());

        for (Neighbour viableBoxPosition : viableBoxPositions) {

            // Find the free cells where the agent can be
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(
                    viableBoxPosition.getPosition(), boxPrecondition.getBox()
            );

            for (Neighbour viableAgentPosition : viableAgentPositions) {
                if(!viableAgentPosition.getPosition().equals(boxPrecondition.getBoxPosition())) {
                    pushActions.add(new PushAction(
                            boxPrecondition.getBox(),
                            boxPrecondition.getAgent(),
                            viableBoxPosition.getPosition(),
                            viableAgentPosition.getPosition(),
                            viableBoxPosition.getDirection().getInverse(),
                            viableAgentPosition.getDirection().getInverse(),
                            LevelService.getInstance().manhattanDistance(
                                    viableAgentPosition.getPosition(),
                                    agentStartPosition
                            )
                    ));
                }
            }
        }

        return pushActions;
    }

    public PriorityQueue<MoveBoxAction> getPullActions(List<Neighbour> viableBoxPositions, BoxAtPrecondition boxPrecondition) {
        PriorityQueue<MoveBoxAction> pullActions = new PriorityQueue<>(new ActionComparator());

        if (viableBoxPositions.size() <= 1) {
            return pullActions;
        }

        for (Neighbour viableBoxPosition : viableBoxPositions) {

            for (Neighbour viableAgentPosition : viableBoxPositions) {
                if (!viableAgentPosition.equals(viableBoxPosition)) {
                    pullActions.add(new PullAction(
                            boxPrecondition.getBox(),
                            boxPrecondition.getAgent(),
                            viableBoxPosition.getPosition(),
                            viableAgentPosition.getPosition(),
                            //TODO: we should change this to the direction that the box was moving in previously
                            viableAgentPosition.getDirection(),
                            viableAgentPosition.getDirection(),
                            LevelService.getInstance().manhattanDistance(
                                    viableAgentPosition.getPosition(),
                                    agentStartPosition
                            )
                    ));
                }
            }
        }

        return pullActions;
    }
}
