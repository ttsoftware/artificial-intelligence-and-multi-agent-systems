package dtu.agency.planners.htn;

import dtu.agency.board.*;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.planners.actions.effects.Effect;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.planners.htn.heuristic.AStarHeuristic;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.searchclient.Memory;
import dtu.searchclient.Node;
import dtu.searchclient.strategy.Strategy;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by koeus on 3/21/16.
 */
public class HTNPlanner {
    // Auxiliary static classes
    public static void error(String msg) throws Exception {
        throw new Exception("HTNError: " + msg);
    }

    // split into two subproblems:
    // 1. move-path to box, initialstate, agentPosition
    // 2. push/pull-path to goal from box, initialState: agentPosition(last from previous), BoxPosition
    // use HTNPlanner-search to find paths

    public HTNNode initialNode = null;

    private PrimitivePlan gotoPlan;
    private PrimitivePlan moveBoxPlan;

    public HTNPlanner(Agent agent, Level level, Box targetBox, Goal target ) throws Exception {

        // define strategy and heuristic - may be injected or set in a different location, main for instance
        Heuristic heuristic = new AStarHeuristic();
        Strategy strategy = new BestFirstStrategy(heuristic);

        // Use stderr to print to console
        System.err.println("HTN planner initializing.");

        // setting general HTNNode settings with copies of the initial state
        HTNNode.visitedEffects = new HashMap<Effect,boolean>(); // Agent position, but might as well be Effect

        // formulate the seperation of problems
        HTNEffect initialGotoState = new HTNEffect(agent.getPosition(), targetBox.getPosition() );
        HLAction gotoAction = new GotoAction(targetBox.getPosition());
        this.initialNode = new HTNNode(null, gotoAction, initialGotoState);

        gotoPlan = plan(initialNode, strategy);
        gotoPlan.removeLast();  // we want to end up in the neighbouring cell, not on top of the box
        // SHIT this is final... should it be?? probably not ?? should be able to correct a plan
        // when replanning, right??

        HTNEffect initialMoveBoxState = gotoPlan.peekLast().getEffect();
        HLAction moveBoxAction = new MoveBoxAction(targetBox, Goal.getPosition());
        this.initialNode = new HTNNode(null, moveBoxAction, initialMoveBoxState);

        moveBoxPlan = plan(initialNode, strategy);

    }

    PrimitivePlan getPlan() {
        // List<PrimitiveAction> totalPlan = new LinkedList<>();
        return merge(gotoPlan, moveBoxPlan);
    }

    public HTNPlan plan() {
        return new HTNPlan(new ArrayList<>());
    }

    public LinkedList<HTNNode> plan(HTNNode initialNode, Strategy strategy) throws IOException {
        System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while (true) {
            if (iterations % 200 == 0) {
                System.err.println(strategy.searchStatus());
            }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 300) { // Minutes timeout
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            Node leafNode = strategy.getAndRemoveLeaf();

            if (leafNode.isGoalState()) {
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) {
                // The list of expanded nodes is shuffled randomly; see Node.java
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

}

// usage:
/*

// Read level and create the initial state of the problem
HTNPlanner planner = new HTNPlanner(agent, level, goal, box);
strategy = new BestFirstStrategy(new WeightedAStarHeuristic(planner.initialState));
LinkedList<HTNNode> solution = planner.plan(strategy);

// from SearchClient
if (solution == null) {
    System.err.println("Unable to solve level");
    System.exit(0);
} else {
    System.err.println("\nSummary for " + strategy);
    System.err.println("Found solution of length " + solution.size());
    System.err.println(strategy.searchStatus());

    for (Node n : solution) {
        String act = n.getAction().toActionString();
        System.out.println(act);
        String response = serverMessages.readLine();
        if (response.contains("false")) {
            System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
            System.err.format("%s was attempted in \n%s\n", act, n);
            break;
        }
    }
}
*/