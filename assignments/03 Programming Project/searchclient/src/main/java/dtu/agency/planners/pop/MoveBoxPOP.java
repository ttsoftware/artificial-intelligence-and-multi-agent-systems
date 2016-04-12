package dtu.agency.planners.pop;

import dtu.agency.agent.actions.*;
import dtu.agency.agent.actions.preconditions.BoxAtPrecondition;
import dtu.agency.agent.actions.preconditions.Precondition;
import dtu.agency.board.*;
import dtu.agency.planners.actions.MoveBoxAbstractAction;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class MoveBoxPOP extends AbstractPOP<MoveBoxAbstractAction> {

    private Position boxStartPosition;

    public MoveBoxPOP(Agent agent) {
        super(agent);
    }


    public POPPlan plan(MoveBoxAbstractAction action) {
        Box box = action.getBox();
        boxStartPosition = LevelService.getInstance().getPosition(box.getLabel());
        Position currentBoxPosition = LevelService.getInstance().getPosition(action.getGoal().getLabel());

        return new POPPlan(getPlan(box, currentBoxPosition, null, new Stack<>()));
    }


    public Stack<Action> getPlan(Box box, Position currentBoxPosition, Position currentAgentPosition, Stack<MoveBoxAction> previousActions){

        Stack<Action> actions = new Stack<>();

        if(currentBoxPosition.isAdjacentTo(boxStartPosition)
                && (currentAgentPosition.equals(boxStartPosition) && !currentBoxPosition.equals(currentAgentPosition)
                || (currentBoxPosition.equals(agentStartPosition) && !agentStartPosition.equals(currentBoxPosition))))
        {
            actions.add(getFirstAction(box, currentBoxPosition, currentAgentPosition));
            return actions;
        }

        Precondition currentPrecondition = new BoxAtPrecondition(box, agent, currentBoxPosition);
        PriorityQueue<MoveBoxAction> stepActions = solvePrecondition((BoxAtPrecondition) currentPrecondition);

        if(!stepActions.isEmpty())
        {
            MoveBoxAction nextAction = stepActions.poll();

            boolean foundCorrectAction = false;

            while(!foundCorrectAction) {
                if (introducesCycle(nextAction, previousActions)) {
                    if (!stepActions.isEmpty()) {
                        nextAction = stepActions.poll();
                    } else {
                        return null;
                    }
                }
                else {
                    foundCorrectAction = true;
                }
            }

            currentBoxPosition = nextAction.getBoxPosition();
            currentAgentPosition = nextAction.getAgentPosition();
            previousActions.add(nextAction);

            actions = getPlan(box, currentBoxPosition, currentAgentPosition, previousActions);

            if(actions != null)
            {
                actions.add(nextAction);
            }
            else
            {
                //TODO: backtrack
                previousActions.remove(nextAction);
            }
        }

        return actions;
    }

    public boolean introducesCycle(MoveBoxAction nextAction, Stack<MoveBoxAction> previousActions)
    {
        for (MoveBoxAction previousAction : previousActions) {
            if (nextAction.getBoxPosition().equals(previousAction.getBoxPosition())) {
                //&& nextAction.getAgentPosition().equals(previousAction.getAgentPosition())))
                return true;
            }
        }
        return false;
    }

    public MoveBoxAction getFirstAction(Box box, Position currentBoxPosition, Position currentAgentPosition) {
        if(!currentBoxPosition.equals(agentStartPosition))
        {
            return new PushAction(
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
            );
        }
        else {
            if (currentAgentPosition == null) {
                currentAgentPosition = LevelService.getInstance().
                        getFreeNeighbours(currentBoxPosition).get(0).getPosition();
            }
            return new PullAction(
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
            );
        }
    }

    /**
     *
     * @param boxPrecondition
     * @return A queue of MoveBoxActions which solves the given precondition
     */
    public PriorityQueue<MoveBoxAction> solvePrecondition(BoxAtPrecondition boxPrecondition) {

        PriorityQueue<MoveBoxAction> actions = new PriorityQueue<>(new ActionComparator());

        List<BoardObject> objectsToIgnore = new ArrayList<>();
        //objectsToIgnore.add(boxPrecondition.getBox());
        objectsToIgnore.add(agent);

        // Find the free neighbour cells to the box
        List<Neighbour> viableBoxPositions = LevelService.getInstance().getFreeNeighbours(
                boxPrecondition.getBoxPosition(), objectsToIgnore
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

        List<BoardObject> objectsToIgnore = new ArrayList<>();
        objectsToIgnore.add(boxPrecondition.getBox());
        objectsToIgnore.add(agent);

        for (Neighbour viableBoxPosition : viableBoxPositions) {

            // Find the free cells where the agent can be
            List<Neighbour> viableAgentPositions = LevelService.getInstance().getFreeNeighbours(
                    viableBoxPosition.getPosition(), objectsToIgnore
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
                            LevelService.getInstance().
                                    manhattanDistance(viableAgentPosition.getPosition(), agentStartPosition)
                                    + LevelService.getInstance().
                                    manhattanDistance(viableBoxPosition.getPosition(), boxStartPosition)
                    ));
                }
            }
        }

        return pushActions;
    }


    //in order to pull a box in the current box's position, the agent must already be there; the only thing that we
    //can control is the place where the agent goes after it has pulled the box in the current goal position
    //this position will be one of the other valid neighbours of the box's goal position
    public PriorityQueue<MoveBoxAction> getPullActions(List<Neighbour> viableBoxPositions, BoxAtPrecondition boxPrecondition) {
        PriorityQueue<MoveBoxAction> pullActions = new PriorityQueue<>(new ActionComparator());

        if (viableBoxPositions.size() <= 1) {
            return pullActions;
        }

        for (Neighbour viableBoxPosition : viableBoxPositions) {

            for (Neighbour viableFutureAgentPosition : viableBoxPositions) {
                if(viableFutureAgentPosition != viableBoxPosition) {
                    pullActions.add(new PullAction(
                            boxPrecondition.getBox(),
                            boxPrecondition.getAgent(),
                            viableBoxPosition.getPosition(),
                            boxPrecondition.getBoxPosition(),
                            //TODO: we should change this to the direction that the box was moving in previously
                            viableFutureAgentPosition.getDirection(),
                            LevelService.getInstance().getMovingDirection(
                                    boxPrecondition.getBoxPosition(), viableFutureAgentPosition.getPosition()),
                            LevelService.getInstance().
                                    manhattanDistance(boxPrecondition.getBoxPosition(), agentStartPosition)
                                    + LevelService.getInstance().
                                    manhattanDistance(viableBoxPosition.getPosition(), boxStartPosition)
                    ));
                }
            }
        }

        return pullActions;
    }
}
