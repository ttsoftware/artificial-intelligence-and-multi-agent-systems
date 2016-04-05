package dtu.agency.planners.htn;


import dtu.Main;
import dtu.agency.agent.actions.NoAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.actions.SolveGoalAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristic;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.heuristic.WeightedAStarHeuristic;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.LevelService;
import java.util.PriorityQueue;

/**
 * Created by Mads on 3/21/16.
 * This Planner uses the Hierarchical Task Network method to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {
    // split into two sub problems:
    // 1. move-path to box, initial state, agentPosition
    // 2. push/pull-path to goal from box, initialState: agentPosition(last from previous), BoxPosition
    // use HTNPlanner-search to find paths

    private Agent agent;                      // agent to perform the actions
    private Goal finalGoal;                   // to check goal state
    PriorityQueue<HTNNode> allInitialNodes;   // list of all possible plans to solve the goal

    /*
    * Constructor: All a planner needs is the next goal and the agent solving it...
    * */
    public HTNPlanner(Agent agent, Goal target ){
        System.err.println("HTN Planner initializing.");
        this.agent = agent;
        this.finalGoal = target;

        Heuristic heuristic = new WeightedAStarHeuristic(Main.heuristicMeasure, 2);
        this.allInitialNodes = createAllNodes(target, heuristic);
        //System.err.println("Nodes: " + allInitialNodes.toString());
    }

    /*
    *  Fills the data structure containing information on ways to solve this particular target
    */
    private PriorityQueue<HTNNode> createAllNodes(Goal target, Heuristic heuristic) {
        //System.err.println("CreateAllNodes: ");
        PriorityQueue<HTNNode> allNodes = new PriorityQueue<>(heuristic);

        for ( Box b : LevelService.getInstance().getLevel().getBoxes() ) {
            if (b.getLabel().toLowerCase().equals(target.getLabel().toLowerCase())) {
                HTNState initialState = new HTNState(agent, b);
                HLAction initialAction = new SolveGoalAction(b, target);
                allNodes.offer( new HTNNode(initialState, initialAction) );
            }
        }
        return allNodes;
    }

    /*
    * is used to find the next plan (using the next box in line)
    */
    public PrimitivePlan plan() {
        PrimitivePlan plan = null;
        for (int i = 0; i < allInitialNodes.size(); i++) {
            plan = rePlan();
            if (!(plan == null)) {
                System.err.println(plan.toString());
                return plan;
            }
        }
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

    /*
    * Checks whether the final goal is reached with a box
    */
    public boolean isGoalState (HTNNode node) {
        return finalGoal.getPosition().equals( node.getState().getBoxPosition() );
    }

    /*
    * This method ensures a viable plan is found for solving a top level goal
    * could introduce relaxation here
    */
    private PrimitivePlan rePlan() { // may return null if no plan is found!
        HTNNode initialNode = allInitialNodes.poll();

        Heuristic heuristic = new AStarHeuristic(Main.heuristicMeasure);
        Strategy strategy = new BestFirstStrategy(heuristic);

        System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while(true) {
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
                System.err.println(strategy.status());
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();
            //System.err.println(leafNode.toString());

            if ( strategy.isExplored(leafNode.getState()) ) {
                // reject nodes resulting in states visited already
                if (leafNode.getAction() instanceof NoAction) {
                    // check for progression
                    HTNNode n;
                    boolean noProgression = true;
                    for (int i = 0 ; i < 5 ; i++) {
                        n = leafNode.getParent(i);
                        if (n==null) {
                            noProgression = false;
                            break;
                        }
                        noProgression &= (n.getAction() instanceof NoAction);
                        //System.err.println(Boolean.toString(noProgression));
                    }
                    if (noProgression) {
                        System.err.println("No progress for 5 nodes! skipping this node");
                        continue;
                    }
                } else {
                    //System.err.println("Effect already explored! skipping this node");
                    continue;
                }
            }

            if (isGoalState(leafNode)) {
                System.err.println("GOOOAAAAAAAALLL!!!!!");
                System.err.println(strategy.status());
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getState());

            for (HTNNode n : leafNode.getRefinementNodes()) {
                System.err.println(n.toString());
                strategy.addToFrontier(n);
            }

            iterations++;
        }

    }

    public int getBestHeuristic() {
        return getBestPlan().getActions().size();
    }
}

