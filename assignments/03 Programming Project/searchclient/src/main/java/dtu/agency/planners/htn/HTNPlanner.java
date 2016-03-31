package dtu.agency.planners.htn;


import dtu.Main;
import dtu.agency.agent.actions.Action;
import dtu.agency.board.*;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.planners.htn.heuristic.AStarHeuristic;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.LevelService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mads on 3/21/16.
 */
public class HTNPlanner {
    // Auxiliary static classes
    public static void error(String msg) throws Exception {
        throw new Exception("HTNError: " + msg);
    }

    // split into two sub problems:
    // 1. move-path to box, initial state, agentPosition
    // 2. push/pull-path to goal from box, initialState: agentPosition(last from previous), BoxPosition
    // use HTNPlanner-search to find paths



    public HTNNode initialNode;         // first node... ?
    ArrayList<HTNPlan> allPlans;        // list of all possible plans to solve the goal
    private HTNPlan bestPlan;           // High level actions only
    private PrimitivePlan moveBoxPlan;  // Low level (primitive) actions only
    private Goal finalGoal;             // to check goal state

    public HTNPlanner(Agent agent, Goal target ){

        // Use stderr to print to console
        System.err.println("HTN Planner initializing.");

        this.finalGoal = target;

        this.allPlans = createAllPlans(target);

        this.bestPlan = findShorterPlan(agent, allPlans);
        // could as well sort them, so that they can be polled one at a time
        Box targetBox = bestPlan.getMoveBoxAction().getBox();

        // The rest is for implementation of complete HTN planning to primitive actions
        // define strategy and heuristic - may be injected or set in a different location, main for instance

        // setting general HTNNode settings with copies of the initial state

        // formulate the separation of problems ,// necessary??
        // NOT Necessary!! The effect should 'spill' from node to node, so when MovetoAction is reached,
        // it can be injected, heuristic should be sum of high level actions... it should be possible
        HTNEffect initialEffect = new HTNEffect(agent.getPosition(), targetBox.getPosition() );
        initialNode = new HTNNode(null, null, initialEffect, new MixedPlan( bestPlan.getActions() ) );

        Heuristic heuristic = new AStarHeuristic(initialEffect, targetBox, target);
        Strategy strategy = new BestFirstStrategy(heuristic);

        moveBoxPlan = plan(strategy);

    }

    public PrimitivePlan getMoveBoxPlan() {
        return moveBoxPlan;
    }

    private HTNPlan findShorterPlan(Agent agent, ArrayList<HTNPlan> allplans) {
        Position a = agent.getPosition();
        HTNPlan bestPlan = allplans.get(0);

        Box targetBox;
        Goal target;
        int minDist;
        int bestHeuristic = Integer.MAX_VALUE;

        for (HTNPlan plan : allplans) {
            targetBox = plan.getMoveBoxAction().getBox();
            target = plan.getMoveBoxAction().getGoal();

            minDist = a.manhattanDist(targetBox.getPosition());
            minDist += targetBox.getPosition().manhattanDist(target.getPosition());
            if (minDist < bestHeuristic) {
                bestHeuristic = minDist;
                bestPlan = plan;
            }
        }
        return bestPlan;
    }

    private ArrayList<HTNPlan> createAllPlans( Goal target) {
        // find all boxes that correspond to goal
        // produce plans [ [goto(B1), MoveTo(B1,g)],...,[goto(B2), MoveTo(B2,g)] ]
        // store this list of 'HTNPlans' to be retrieved by a method
        ArrayList<HTNPlan> allPlans = new ArrayList<>();
        ArrayList<Box> boxes = new ArrayList<>();

        for (Box b : LevelService.getInstance().getLevel().getBoxes() ) {
            if ( b.getLabel().toLowerCase().equals(target.getLabel().toLowerCase()) ) {
                boxes.add(b);
            }
        }

        HTNPlan tempPlan;
        for (Box b : boxes) {
            tempPlan = new HTNPlan(new GotoAction(b), new MoveBoxAction(b, target));
            allPlans.add(tempPlan);
            tempPlan.clearActions();
        }
        return allPlans;
    }


    public boolean isGoalState (HTNNode node) {
        return finalGoal.getPosition().equals( node.getEffect().getBoxPosition() );
    }

    //public PrimitivePlan plan(Strategy strategy) throws IOException {
    public PrimitivePlan plan(Strategy strategy) {
        System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while(true) {
            if (iterations % Main.printIterations == 0) {
                System.err.println(strategy.status());
            }

            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }

            if (strategy.timeSpent() > Main.timeOut ) {
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();

            if (strategy.isExplored(leafNode.getEffect()) ) { continue; } // reject nodes resulting in states visited already

            if (isGoalState(leafNode)) {
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getEffect());

            // beginning
            for (HTNNode n : leafNode.getRefinementNodes()) {
                // The list of expanded nodes is shuffled randomly; see Node.java
                // and it might be empty!
                if (strategy.isExplored(n.getEffect()) ) { continue; } // reject/ignore nodes resulting in states visited already
                if (strategy.inFrontier(n)) { continue; }              // check if node is already in frontier ?? but how could it be??

                strategy.addToFrontier(n);
            }
            // end
            iterations++;
        }

    }

    public HTNPlan getBestPlan() {
        return bestPlan;
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
            System.err.format("Server responded with %s to the inapplicable action: %s\n", response, act);
            System.err.format("%s was attempted in \n%s\n", act, n);
            break;
        }
    }
}
*/