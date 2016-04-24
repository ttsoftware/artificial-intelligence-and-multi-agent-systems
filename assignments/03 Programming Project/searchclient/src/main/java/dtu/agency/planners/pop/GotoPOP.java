package dtu.agency.planners.pop;

import dtu.agency.actions.BlockingGoalsAndActions;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.ActionComparator;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.pop.preconditions.AgentAtPrecondition;
import dtu.agency.planners.pop.preconditions.Precondition;
import dtu.agency.actions.abstractaction.GotoAbstractAction;
import dtu.agency.services.LevelService;

import java.util.*;

public class GotoPOP extends AbstractPOP<GotoAbstractAction> {

    private Position agentStartPosition;

    public GotoPOP(Agent agent) {
        super(agent);
        agentStartPosition = LevelService.getInstance().getBestGoalWeighingPosition();
    }

    public List<PriorityQueue<Goal>> getWeighedGoals() {

        List<PriorityQueue<Goal>> goalQueueList = new ArrayList<>();

        List<Goal> levelGoals = LevelService.getInstance().getLevel().getGoals();
        List<Goal> handledGoals = new ArrayList<>();

        for (Goal goal : levelGoals) {
            if (!handledGoals.contains(goal)) {
                List<Goal> blockingGoalsList = getBlockingGoals(goal.getPosition());
                blockingGoalsList.add(0, goal);

                List<PriorityQueue<Goal>> newQueueList = getNonSelfStandingGoalList(blockingGoalsList, goalQueueList, handledGoals);
                if(newQueueList == null)
                {
                    PriorityQueue<Goal> blockingGoalsPriorityQueue = getPriorityQueueFromList(blockingGoalsList);
                    goalQueueList.add(blockingGoalsPriorityQueue);
                }
                else{
                    goalQueueList = newQueueList;
                }


                handledGoals.addAll(blockingGoalsList);
            }
        }

        return goalQueueList;
    }

    public List<Goal> getBlockingGoals(Position goalPosition)
    {
        return (getBlockingGoals(goalPosition,
                new BlockingGoalsAndActions(new Stack(), new ArrayList<>()), false)).getBlockingGoals();
    }

    public List<PriorityQueue<Goal>> getNonSelfStandingGoalList(List<Goal> currentGoalList, List<PriorityQueue<Goal>> goalQueuesList, List<Goal> handledGoals) {

        for (int i = 0; i < currentGoalList.size(); i++) {
            Goal goal = currentGoalList.get(i);
            if (handledGoals.contains(goal)) {

                for (PriorityQueue<Goal> goalQueue : goalQueuesList) {
                    if (goalQueue.contains(goal)) {
                        int firstRepetitiveGoalWeight = -1;

                        Iterator<Goal> it = goalQueue.iterator();
                        while (it.hasNext()) {
                            Goal g = it.next();
                            if (g.getLabel().equals(goal.getLabel())) {
                                firstRepetitiveGoalWeight = g.getWeight();
                                break;
                            }
                        }

                        if (firstRepetitiveGoalWeight != -1) {
                            int repetitiveGoalIndex = currentGoalList.indexOf(goal);
                            int goalWeight = firstRepetitiveGoalWeight - 1;

                            for (int j = repetitiveGoalIndex - 1; j >= 0; j--) {
                                Goal goalToBeInserted = currentGoalList.get(j);
                                goalToBeInserted.setWeight(goalWeight--);
                                goalQueue.add(goalToBeInserted);
                            }
                        }

                        return goalQueuesList;
                    }
                }
            }
        }

        return null;
    }


    public PriorityQueue<Goal> getPriorityQueueFromList(List<Goal> goals)
    {
        PriorityQueue<Goal> weightedGoals = new PriorityQueue<>(new GoalComparator());

        goals.get(0).setWeight(0);

        for(int i = 0; i < goals.size(); i++)
        {
            Goal goal = goals.get(i);
            goal.setWeight(i + 1);
            weightedGoals.add(goal);
        }

        return weightedGoals;
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
        PriorityQueue<MoveConcreteAction> stepAdditionalActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

        if (stepActions.isEmpty() && !canBacktrack) {
            if (!stepAdditionalActions.isEmpty()) {
                MoveConcreteAction nextAction = stepAdditionalActions.poll();

                currentBlockingGoals.add((Goal) LevelService.getInstance().getLevel().getBoardObjects()
                        [nextAction.getAgentPosition().getRow()][nextAction.getAgentPosition().getColumn()]);
                previousActions.add(nextAction);

                blockingGoalsAndActions = getBlockingGoals(nextAction.getAgentPosition(), blockingGoalsAndActions, canBacktrack);
                return blockingGoalsAndActions;
            } else {
                System.err.println("The level is not solvable");
            }
        }

        boolean foundNextAction = false;

        while (!stepActions.isEmpty() && !foundNextAction) {
            stepActions = eliminateFirstIncorrectActions(stepActions, blockingGoalsAndActions);
            if (stepActions != null && !stepActions.isEmpty()) {
                MoveConcreteAction nextAction = stepActions.poll();

                previousActions.add(nextAction);
                canBacktrack = canBacktrack || stepActions.size() > 0;

                if (getBlockingGoals(nextAction.getAgentPosition(), blockingGoalsAndActions, canBacktrack) == null) {
                    previousActions.remove(nextAction);
                } else {
                    foundNextAction = true;
                }
            }
        }

       while(!foundNextAction && !stepAdditionalActions.isEmpty()) {
           stepAdditionalActions = eliminateFirstIncorrectActions(stepAdditionalActions, blockingGoalsAndActions);
           if (stepAdditionalActions != null && !stepAdditionalActions.isEmpty()) {
               MoveConcreteAction nextAction = stepAdditionalActions.poll();
               previousActions.add(nextAction);
               currentBlockingGoals.add((Goal) LevelService.getInstance().getLevel().getBoardObjects()
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

        return blockingGoalsAndActions;
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

    /**
     *
     * @param precondition
     * @return A queue of MoveActions which solves the given precondition
     */
    public PriorityQueue<MoveConcreteAction> solvePrecondition(AgentAtPrecondition precondition) {
        PriorityQueue<MoveConcreteAction> concreteActions = new PriorityQueue<>(new ActionComparator());

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

    /**
     *
     * @param precondition
     * @return the list of further actions that can be taken, including moving to goal cells
     */
    public PriorityQueue<MoveConcreteAction> solvePreconditionForGoal(AgentAtPrecondition precondition) {
        PriorityQueue<MoveConcreteAction> concreteActions = new PriorityQueue<>(new ActionComparator());

        List<Neighbour> neighbours = LevelService.getInstance().getNonGoalFreeNeighbours(
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

    public POPPlan plan(GotoAbstractAction gotoAbstractAction)
    {
        Position goalPosition = gotoAbstractAction.getPosition();
        agentStartPosition = LevelService.getInstance().getPosition(agent.getLabel());

        return new POPPlan(getPlan(goalPosition, new Stack<>()));
    }


    /**
     *
     * @param currentAgentPosition
     * @param previousActions
     * @return a stack of actions for moving from a position to a goal position
     */
    public Stack<ConcreteAction> getPlan(Position currentAgentPosition, Stack<MoveConcreteAction> previousActions) {
        Stack<ConcreteAction> actions = new Stack<>();

        Precondition currentPrecondition = new AgentAtPrecondition(agent, currentAgentPosition);

        if (currentAgentPosition.isAdjacentTo(agentStartPosition)) {
            actions.add(new MoveConcreteAction(agent, currentAgentPosition, LevelService.getInstance()
                    .getMovingDirection(agentStartPosition, currentAgentPosition), 0));
            return actions;
        }

        PriorityQueue<MoveConcreteAction> stepActions = solvePrecondition((AgentAtPrecondition) currentPrecondition);

        boolean foundNextAction = false;

        while(!stepActions.isEmpty() && !foundNextAction) {
            MoveConcreteAction nextAction = (MoveConcreteAction) stepActions.poll();

            boolean foundCorrectAction = false;

            while (!foundCorrectAction) {
                if (introducesCycle(nextAction, previousActions)) {
                    if (!stepActions.isEmpty()) {
                        nextAction = stepActions.poll();
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
}
