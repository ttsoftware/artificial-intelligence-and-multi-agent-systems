package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristicComparator;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.planners.htn.heuristic.WeightedAStarHeuristicComparator;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.LevelService;

import java.util.PriorityQueue;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {
    // split into two sub problems:
    // 1. move-path to box, initial state, agentPosition
    // 2. push/pull-path to goal from box, initialState: agentPosition(last from previous), BoxPosition
    // use HTNPlanner-search to find paths

    private Agent agent;                      // agent to perform the actions
    private Goal finalGoal;                   // to check goal state
    PriorityQueue<HTNNode> allInitialNodes;   // list of all possible plans to solve the goal
    HeuristicComparator aStarHeuristicComparator;

    /*
    * Constructor: All a planner needs is the next goal and the agent solving it...
    * */
    public HTNPlanner(Agent agent, Goal target) {
        //System.err.println("HTN Planner initializing.");
        this.agent = agent;
        this.finalGoal = target;
        aStarHeuristicComparator = new AStarHeuristicComparator(Main.heuristicMeasure);
        this.allInitialNodes = createAllNodes(target, aStarHeuristicComparator);
        //System.err.println("Nodes: " + allInitialNodes.toString());
    }

    /*
    *  Fills the data structure containing information on ways to solve this particular target
    */
    private PriorityQueue<HTNNode> createAllNodes(Goal target, HeuristicComparator heuristicComparator) {
        //System.err.println("CreateAllNodes: ");
        PriorityQueue<HTNNode> allNodes = new PriorityQueue<>(heuristicComparator);

        for (Box box : LevelService.getInstance().getLevel().getBoxes()) {
            if (box.getLabel().toLowerCase().equals(target.getLabel().toLowerCase())) {
                HTNState initialState = new HTNState(
                        LevelService.getInstance().getPosition(agent),
                        LevelService.getInstance().getPosition(box)
                );
                HLAction initialAction = new SolveGoalAction(box, target);
                allNodes.offer(new HTNNode(initialState, initialAction));
            }
        }
        return allNodes;
    }

    /*
    * is used to find the next plan (using the next box in line)
    */
    public PrimitivePlan plan() {
        PrimitivePlan plan = null;
        //System.err.println("size of allinitialnodes:" + allInitialNodes.size());
        for (int i = 0; i < allInitialNodes.size(); i++) {
            plan = rePlan();
            if (!(plan == null)) {
                //System.err.println("HELLO" + plan.toString());
                return plan;
            }
        }
        //System.err.println("HELLO" + plan.toString());
        return null;
    }

    /*
    * Returns the best suited HTN plan for use with other planner mechanisms
    */
    public HTNPlan getBestPlan() {
        HTNNode node = allInitialNodes.peek();
        SolveGoalAction action = (SolveGoalAction) node.getRemainingPlan().getActions().getFirst();
        HTNState state = node.getState();
        MixedPlan plan = action.getRefinements(state).get(0);
        return new HTNPlan(plan);
    }

    public int getBestPlanApproximation() {
        return aStarHeuristicComparator.h( allInitialNodes.peek() );
    }

    /*
    * Checks whether the final goal is reached with a box
    */
    public boolean isGoalState(HTNNode node) {
        return finalGoal.getPosition().equals(node.getState().getBoxPosition());
    }

    /*
    * This heuristic ensures a viable plan is found for solving a top level goal
    * could introduce relaxation here
    */
    private PrimitivePlan rePlan() { // may return null if no plan is found!
        HTNNode initialNode = allInitialNodes.poll();
        Strategy strategy = new BestFirstStrategy(aStarHeuristicComparator);

        //System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while (true) {
            if (iterations % Main.printIterations == 0) {
                System.err.println(strategy.status());
            }
            /*
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                System.err.println(strategy.status());
                return null;
            }

            if (strategy.timeSpent() > Main.timeOut ) {
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                System.err.println(strategy.status());
                return null;
            }*/

            if (strategy.frontierIsEmpty()) {
                System.err.format("Frontier is empty, HTNPlanner failed to create a plan!\n");
                //System.err.println(strategy.status());
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();
            //System.err.println(leafNode.toString());
            //System.err.println(strategy.status());

            if (strategy.isExplored(leafNode.getState())) {
                // reject nodes resulting in states visited already
                if (leafNode.getConcreteAction() instanceof NoConcreteAction) {
                    // check for progression
                    HTNNode n;
                    boolean noProgression = true;
                    for (int i = 0; i < 5; i++) {
                        n = leafNode.getParent(i);
                        if (n == null) {
                            noProgression = false;
                            break;
                        }
                        noProgression &= (n.getConcreteAction() instanceof NoConcreteAction);
                        //System.err.println(Boolean.toString(noProgression));
                    }
                    if (noProgression) {
                        //System.err.println("No progress for 5 nodes! skipping this node");
                        continue;
                    }
                } else {
                    //System.err.println("Effect already explored! skipping this node");
                    continue;
                }
                //System.err.println("Effect already explored, but NoActions, so still interesting!");
            }

            if (isGoalState(leafNode)) {
                System.err.println("GOOOAAAAAAAALLL!!!!!");
                System.err.println(strategy.status());
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getState());

            for (HTNNode n : leafNode.getRefinementNodes()) {
                //System.err.println("Adding refinement node:" + n.toString());
                strategy.addToFrontier(n);
            }

            iterations++;
        }
    }
}

