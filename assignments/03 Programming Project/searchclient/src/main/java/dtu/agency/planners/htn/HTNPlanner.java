package dtu.agency.planners.htn;


import dtu.Main;
import dtu.agency.agent.actions.NoAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristic;
import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.LevelService;

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



    private Agent agent;
    public HTNNode initialNode;         // first node... ?
    ArrayList<HTNPlan> allPlans;        // list of all possible plans to solve the goal
    private HTNPlan bestPlan;           // High level actions only
    //private PrimitivePlan moveBoxPlan;  // Low level (primitive) actions only
    private Goal finalGoal;             // to check goal state

    public HTNPlanner(Agent agent, Goal target ){
        this.agent = agent;
        this.finalGoal = target;

        // Use stderr to print to console
        System.err.println("HTN Planner initializing.");

        //System.err.println("Agent Found at: " + agent.toString());
        //System.err.println("Goal  Found at: " + finalGoal.toString());

        this.allPlans = createAllPlans(target);
        //System.err.println("All Plans Generated: " + allPlans.toString());

        rePlan();  // could as well sort them, so that they can be polled one at a time - NO! search linearly, as expected time is O(N)
        System.err.println("BestPlan found:" + bestPlan.toString());
    }

    private ArrayList<HTNPlan> createAllPlans(Goal target) {
        // find all boxes that correspond to goal
        // produce plans [ [goto(B1), MoveTo(B1,g)],...,[goto(B2), MoveTo(B2,g)] ]
        // store this list of 'HTNPlans' to be retrieved by a method
        ArrayList<HTNPlan> allPlans = new ArrayList<>();
        ArrayList<Box> boxes = new ArrayList<>();
        //System.err.println("Creating All Plans: ");

        for (Box b : LevelService.getInstance().getLevel().getBoxes() ) {
            if ( b.getLabel().toLowerCase().equals(target.getLabel().toLowerCase()) ) {
                boxes.add(b);
            }
        }
        //System.err.println("Boxes found: ");
        //System.err.println(boxes.toString());

        for (Box b : boxes) {

            GotoAction gta = new GotoAction(b);
            //System.err.println("GotoAction created: ");
            //System.err.println(gta.toString());

            MoveBoxAction mba = new MoveBoxAction(b, target);
            //System.err.println("MoveBoxAction created: ");
            //System.err.println(mba.toString());

            allPlans.add(new HTNPlan(gta, mba));
        }
        //System.err.println("AllPlans created: ");
        //System.err.println(allPlans.toString());
        return allPlans;
    }

    private void rePlan() {
        Position a = agent.getPosition();
        if (allPlans.isEmpty()) {
            this.bestPlan = null;
            return;
        }
        HTNPlan bestPlan = allPlans.get(0);

        Box targetBox;
        Goal target;
        int minDist;
        int bestHeuristic = Integer.MAX_VALUE;

        for (HTNPlan plan : allPlans) {
            targetBox = plan.getMoveBoxAction().getBox();
            target = plan.getMoveBoxAction().getGoal();

            minDist = a.manhattanDist(targetBox.getPosition());
            minDist += targetBox.getPosition().manhattanDist(target.getPosition());
            if (minDist < bestHeuristic) {
                bestHeuristic = minDist;
                bestPlan = plan;
            }
        }
        allPlans.remove(bestPlan);
        this.bestPlan = bestPlan;
    }

    public boolean isGoalState (HTNNode node) {
        return finalGoal.getPosition().equals( node.getState().getBoxPosition() );
    }

    public PrimitivePlan plan() { // may return null if no plan is found!
        Box targetBox = bestPlan.getMoveBoxAction().getBox();

        HTNState initialEffect = new HTNState(agent.getPosition(), targetBox.getPosition() );

        initialNode = new HTNNode(null, null, initialEffect, new MixedPlan(bestPlan.getActions()) );

        Heuristic heuristic = new AStarHeuristic(initialEffect, targetBox, finalGoal);
        Strategy strategy = new BestFirstStrategy(heuristic);

        System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while(true) {
            if (iterations % Main.printIterations == 0) {
                System.err.println(strategy.status());
            }

            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                System.err.println(strategy.status());
                return null;
            }

            if (strategy.timeSpent() > Main.timeOut ) {
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                System.err.println(strategy.status());
                return null;
            }

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
                        n = leafNode.getParent();
                        noProgression &= (n.getAction() instanceof NoAction);
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
                strategy.addToFrontier(n);
            }

            iterations++;
        }

    }

    public HTNPlan getBestPlan() {
        return bestPlan;
    }
}

