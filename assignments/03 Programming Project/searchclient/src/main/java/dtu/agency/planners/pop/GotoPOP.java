package dtu.agency.planners.pop;

import dtu.agency.actions.BlockingGoalsAndActions;
import dtu.agency.actions.abstractaction.actioncomparators.ConcreteActionComparator;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.pop.preconditions.AgentAtPrecondition;
import dtu.agency.planners.pop.preconditions.Precondition;
import dtu.agency.services.GlobalLevelService;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class GotoPOP {

    private PriorityQueue<Position> goalWeighingPositions;
    private Position agentStartPosition;
    private Agent agent;
    
    public GotoPOP() {
        agent = new Agent("-1");
        goalWeighingPositions = GlobalLevelService.getInstance().getBestGoalWeighingPositionsList();
        agentStartPosition = goalWeighingPositions.poll();
    }

    public List<PriorityBlockingQueue<Goal>> getWeighedGoals() {

        List<PriorityBlockingQueue<Goal>> goalQueueList = new ArrayList<>();

        List<Goal> levelGoals = GlobalLevelService.getInstance().getLevel().getGoals();
        Set<Goal> handledGoals = new HashSet<>();

        for (Goal goal : levelGoals) {
            if (!handledGoals.contains(goal)) {
                List<Goal> blockingGoalsList = getBlockingGoals(goal.getPosition());

                //sometimes the planner finds a path that goes back in a circle through the goal. I have decided
                //that instead of backtracking, I'm just going to leave it find the correct path from that point on
                //P.S. it never includes actions that form a loop, apart from the case in which the loop begins and
                //ends in the goal
                if(!blockingGoalsList.contains(goal)){
                    blockingGoalsList.add(0, goal);
                }

                goalQueueList = mergePriorityQueues(goal, blockingGoalsList, goalQueueList);

                handledGoals.addAll(blockingGoalsList);
            }
        }

        return goalQueueList;
    }

    public List<Goal> getBlockingGoals(Position goalPosition)
    {
        BlockingGoalsAndActions blockingGoalsAndActions = new BlockingGoalsAndActions(new Stack(), new ArrayList<>());
        blockingGoalsAndActions = getBlockingGoals(goalPosition, new BlockingGoalsAndActions(new Stack(), new ArrayList<>()), false);

        while(blockingGoalsAndActions == null) {
            agentStartPosition = goalWeighingPositions.poll();
            blockingGoalsAndActions = getBlockingGoals(goalPosition, new BlockingGoalsAndActions(new Stack(), new ArrayList<>()), false);
        }

        return blockingGoalsAndActions.getBlockingGoals();
    }

    public List<PriorityBlockingQueue<Goal>> mergePriorityQueues(Goal blockedGoal, List<Goal> blockingGoals, List<PriorityBlockingQueue<Goal>> priorityQueues)
    {
        List<PriorityBlockingQueue<Goal>> finalPriorityQueues = new ArrayList<>();

        PriorityBlockingQueue<Goal> mergedPriorityQueue = new PriorityBlockingQueue<>();
        mergedPriorityQueue.add(blockedGoal);

        for (PriorityBlockingQueue<Goal> priorityQueue : priorityQueues) {
            boolean disjoint = true;
            for (int i = 0; i < blockingGoals.size(); i++) {
                Goal blockingGoal = blockingGoals.get(i);

                if (priorityQueue.contains(blockingGoal)) {
                    int weightDifference = i - blockingGoal.getWeight();

                    Iterator<Goal> priorityQueueIterator = priorityQueue.iterator();

                    while (priorityQueueIterator.hasNext()) {
                        Goal priorityQueueGoal = priorityQueueIterator.next();

                        priorityQueueGoal.setWeight(weightDifference + priorityQueueGoal.getWeight());
                        mergedPriorityQueue.add(priorityQueueGoal);
                    }

                    disjoint = false;

                    break;
                }
            }

            if(disjoint) {
                finalPriorityQueues.add(priorityQueue);
            }
        }

        for(int i = 0; i < blockingGoals.size(); i++) {
            Goal blockingGoal = blockingGoals.get(i);
            if(!mergedPriorityQueue.contains(blockingGoal)) {
                blockingGoal.setWeight(i);
                mergedPriorityQueue.add(blockingGoal);
            }
        }

        finalPriorityQueues.add(mergedPriorityQueue);

        return finalPriorityQueues;
    }

    /**
     *
     * @param currentAgentPosition
     * @param blockingGoalsAndActions
     * @param canBacktrack
     * @return an object that contains the actions that can be taken to reach a goal from a position, and a list of
     * goals that are "in the way"
     */
    public BlockingGoalsAndActions getBlockingGoals(Position currentAgentPosition,
                                                    BlockingGoalsAndActions blockingGoalsAndActions,
                                                    boolean canBacktrack) {

        Precondition currentPrecondition = new AgentAtPrecondition(agent, currentAgentPosition);
        List<Goal> currentBlockingGoals = blockingGoalsAndActions.getBlockingGoals();
        Stack<MoveConcreteAction> previousActions = blockingGoalsAndActions.getActions();

        if (currentAgentPosition.isAdjacentTo(agentStartPosition)) {
            return blockingGoalsAndActions;
        }

        PriorityQueue<MoveConcreteAction> stepActions = solvePreconditionForGoal((AgentAtPrecondition) currentPrecondition);
        PriorityQueue<MoveConcreteAction> stepAdditionalActions = solvePreconditionWithGoals((AgentAtPrecondition) currentPrecondition);

        if (stepActions.isEmpty() && !canBacktrack) {
            stepAdditionalActions = eliminateFirstIncorrectActions(stepAdditionalActions, blockingGoalsAndActions);
            if (!stepAdditionalActions.isEmpty()) {
                MoveConcreteAction nextAction = stepAdditionalActions.poll();

                currentBlockingGoals.add((Goal) GlobalLevelService.getInstance().getLevel().getBoardObjects()
                        [nextAction.getAgentPosition().getRow()][nextAction.getAgentPosition().getColumn()]);
                previousActions.add(nextAction);

                canBacktrack = canBacktrack || stepAdditionalActions.size() > 0;
                blockingGoalsAndActions = getBlockingGoals(nextAction.getAgentPosition(), blockingGoalsAndActions, canBacktrack);
                return blockingGoalsAndActions;
            } else {
                //System.err.println("The level is not solvable");
            }
        }

        boolean foundNextAction = false;

        while (!stepActions.isEmpty() && !foundNextAction) {
            stepActions = eliminateFirstIncorrectActions(stepActions, blockingGoalsAndActions);
            if (stepActions != null && !stepActions.isEmpty()) {
                MoveConcreteAction nextAction = stepActions.poll();

                previousActions.add(nextAction);
                canBacktrack = canBacktrack || stepActions.size() > 0 || stepAdditionalActions.size() > 0;

                if (getBlockingGoals(nextAction.getAgentPosition(), blockingGoalsAndActions, canBacktrack) == null) {
                    previousActions.remove(nextAction);
                } else {
                    foundNextAction = true;
                }
            }
        }

        while (!foundNextAction && !stepAdditionalActions.isEmpty()) {
            stepAdditionalActions = eliminateFirstIncorrectActions(stepAdditionalActions, blockingGoalsAndActions);
            if (stepAdditionalActions != null && !stepAdditionalActions.isEmpty()) {
                MoveConcreteAction nextAction = stepAdditionalActions.poll();
                previousActions.add(nextAction);
                currentBlockingGoals.add((Goal) GlobalLevelService.getInstance().getLevel().getBoardObjects()
                        [nextAction.getAgentPosition().getRow()][nextAction.getAgentPosition().getColumn()]);

                canBacktrack = canBacktrack || stepAdditionalActions.size() > 0;

                if (getBlockingGoals(nextAction.getAgentPosition(), blockingGoalsAndActions, canBacktrack) == null) {
                    previousActions.remove(nextAction);
                    currentBlockingGoals.remove(currentBlockingGoals.size() - 1);
                } else {
                    foundNextAction = true;
                }
            } else {
                return null;
            }
        }

        if (foundNextAction) {
            return blockingGoalsAndActions;
        } else {
            //TODO:Backtrack
            return null;
        }
    }

    /**
     *
     * @param stepActions
     * @param blockingGoalsAndActions
     * @return The priority queue of actions where the leading actions that introduce cycles were removed
     */
    public PriorityQueue<MoveConcreteAction> eliminateFirstIncorrectActions(PriorityQueue<MoveConcreteAction> stepActions,
                                                                           BlockingGoalsAndActions blockingGoalsAndActions) {
        MoveConcreteAction nextAction = stepActions.peek();

        boolean foundCorrectAction = false;

        while (!foundCorrectAction) {
            if (introducesCycle(nextAction, blockingGoalsAndActions.getActions())) {
                stepActions.poll();

                if (!stepActions.isEmpty()) {
                    nextAction = stepActions.peek();
                } else {
                    return new PriorityQueue<>();
                }
            } else {
                foundCorrectAction = true;
            }
        }

        return stepActions;
    }

    /**
     *
     * @param nextAction
     * @param previousActions
     * @return true if an action introduces a cycle and false otherwise
     */
    public boolean introducesCycle(MoveConcreteAction nextAction, Stack<MoveConcreteAction> previousActions)
    {
        for (MoveConcreteAction previousAction : previousActions) {
            if (nextAction.getAgentPosition().equals(previousAction.getAgentPosition())) {
                return true;
            }
        }
        return false;
    }

    public PriorityQueue<MoveConcreteAction> solvePreconditionWithGoals(AgentAtPrecondition precondition)
    {
        PriorityQueue<MoveConcreteAction> concreteActions = new PriorityQueue<>(new ConcreteActionComparator());

        List<Neighbour> neighbours = GlobalLevelService.getInstance().getGoalFreeNeighbours(
                precondition.getAgentPreconditionPosition()
        );

        for (Neighbour neighbour : neighbours) {
            MoveConcreteAction nextAction = new MoveConcreteAction(
                    agent,
                    neighbour.getPosition(),
                    neighbour.getDirection(),
                    GlobalLevelService.getInstance().manhattanDistance(neighbour.getPosition(), agentStartPosition)
            );
            concreteActions.add(nextAction);
        }

        return concreteActions;
    }

    /**
     *
     * @param precondition
     * @return the list of further actions that can be taken, including moving to goal cells
     */
    public PriorityQueue<MoveConcreteAction> solvePreconditionForGoal(AgentAtPrecondition precondition) {
        PriorityQueue<MoveConcreteAction> concreteActions = new PriorityQueue<>(new ConcreteActionComparator());

        List<Neighbour> neighbours = GlobalLevelService.getInstance().getNonGoalFreeNeighbours(
                precondition.getAgentPreconditionPosition()
        );

        for (Neighbour neighbour : neighbours) {
            MoveConcreteAction nextAction = new MoveConcreteAction(
                    agent,
                    neighbour.getPosition(),
                    neighbour.getDirection(),
                    GlobalLevelService.getInstance().manhattanDistance(neighbour.getPosition(), agentStartPosition)
            );
            concreteActions.add(nextAction);
        }

        return concreteActions;
    }
}
