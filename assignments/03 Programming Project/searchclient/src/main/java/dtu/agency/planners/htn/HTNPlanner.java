package dtu.agency.planners.htn;


import dtu.agency.agent.actions.Action;
import dtu.agency.board.*;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.planners.htn.heuristic.AStarHeuristic;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;

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


    ArrayList<HTNPlan> hlplans;

    public HTNNode initialNode = null;

    // public HTNNode initialGotoNode = null;
    // public HTNNode initialMoveBoxNode = null;

    private PrimitivePlan gotoPlan;
    private PrimitivePlan moveBoxPlan;

    private HTNPlan bestPlan;
    private PrimitivePlan llplan;


    public HTNPlanner(Agent agent, Level level, Goal target ) throws Exception {

        // Use stderr to print to console
        System.err.println("HTN Planner initializing.");

        // find all boxes that correspond to goal
        // produce plans [ [goto(B1), MoveTo(B1,g)],...,[goto(B2), MoveTo(B2,g)] ]
        // store this list of 'HTNPlans' to be retrieved by a method

        this.hlplans = new ArrayList<>();
        ArrayList<Box> boxes = new ArrayList<>();

        for (Box b : level.getBoxes() ) {
            if ( b.getLabel().toLowerCase().equals(target.getLabel().toLowerCase()) ) {
                boxes.add(b);
            }
        }

        // part of following experiment
        Position a = agent.getPosition();
        Box targetBox = boxes.get(0);
        this.bestPlan = new HTNPlan(new ArrayList<>() );
        this.bestPlan.addAction(new GotoAction(targetBox));
        this.bestPlan.addAction(new MoveBoxAction(targetBox, target) ) ;
        int bestHeuristic = a.manhattanDist(targetBox.getPosition());
        bestHeuristic += targetBox.getPosition().manhattanDist(target.getPosition());
        int mdist;
        // end of experiment part

        HTNPlan tempPlan = new HTNPlan(new ArrayList<>() );
        for (Box b : boxes) {

            tempPlan.addAction(new GotoAction(b) ) ;
            tempPlan.addAction(new MoveBoxAction(b, target) ) ;

            hlplans.add(tempPlan);
            tempPlan.clearActions();

            // part of following experiment
            mdist = a.manhattanDist(targetBox.getPosition());
            mdist += b.getPosition().manhattanDist(target.getPosition());
            if (mdist < bestHeuristic) {
                bestHeuristic = mdist;
                targetBox = b;
                this.bestPlan = tempPlan;
            }
            // end of experiment part
        }

        // The rest is for implementation of complete HTN planning to primitive actions
        // define strategy and heuristic - may be injected or set in a different location, main for instance


        // setting general HTNNode settings with copies of the initial state
        // HTNNode.visitedEffects.clear(); // relocate to strategy??

        // formulate the seperation of problems ,// necessary??
        // NOT Necessary!! The effect should 'spill' from node to node, so when MovetoAction is reached,
        // it can be injected, heuristic should be sum of hlactions... it should be possible
        HTNEffect initialEffect = new HTNEffect(agent.getPosition(), targetBox.getPosition() );
        initialNode = new HTNNode(null, null, initialEffect, bestPlan);
        Heuristic heuristic = new AStarHeuristic(initialEffect, targetBox, target);
        Strategy strategy = new BestFirstStrategy(heuristic);

        moveBoxPlan = plan(strategy);

        /* divided, to treat special remove one action at the end of gotoAction
        HLAction gotoAction = new GotoAction(targetBox);
        initialGotoNode = new HTNNode(null, gotoAction, initialEffect);
        Heuristic heuristic = new AStarHeuristic(initialGotoNode);
        Strategy strategy = new BestFirstStrategy(heuristic);

        gotoPlan = plan(strategy);
        gotoPlan.getActions().removeLast(); // we want to end up in the neighbouring cell, not on top of the box

        //HTNEffect initialMoveBoxState = gotoPlan.actions.peekLast().getEffect(); // how the heck are we gonna get this?

        HTNEffect initialMoveBoxState = result(initialEffect, gotoPlan ); // how the heck are we gonna get this?
        HLAction moveBoxAction = new MoveBoxAction(targetBox, Goal.getPosition());
        initialMoveBoxNode = new HTNNode(null, moveBoxAction, initialMoveBoxState);
        heuristic = new AStarHeuristic(initialMoveBoxNode);
        strategy = new BestFirstStrategy(heuristic);

        moveBoxPlan = plan(strategy);

        */
    }



    public HTNPlan plan(Position initialPosition){
        HTNPlan bestPlan = null;
        int bestHeuristic = Integer.MAX_VALUE;
        int heuristic;
        for (HTNPlan p : this.hlplans) {
            GotoAction agoto = (GotoAction) p.getActions().get(0);
            MoveBoxAction amove = (MoveBoxAction) p.getActions().get(1);

            heuristic = Position.manhattanDist(initialPosition, agoto.getDestination());
            heuristic += Position.manhattanDist(agoto.getDestination(), amove.getDestination());

            if (heuristic < bestHeuristic) {
                bestHeuristic = heuristic;
                bestPlan = p;
            }
        }
        return bestPlan;
    }


    HTNEffect result(HTNEffect initialEffect, PrimitivePlan plan) {
        // should yield another effect representing the (changes in) state of relevant objects in the world
        ;
    }

    HTNEffect result(HTNEffect initialEffect, Action action) {
        // should yield another effect representing the (changes in) state of relevant objects in the world
        ;
    }

    PrimitivePlan getPlan() {
        // List<PrimitiveAction> totalPlan = new LinkedList<>();
        // should not be necessary! - let the HTN algorithm do this...
        return merge(gotoPlan, moveBoxPlan);
    }

    public PrimitivePlan plan(Strategy strategy) throws IOException {
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