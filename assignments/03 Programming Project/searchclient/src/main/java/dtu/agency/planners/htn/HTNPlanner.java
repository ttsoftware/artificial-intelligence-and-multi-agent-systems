package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.agent.bdi.Belief;
import dtu.agency.board.*;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristicComparator;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.LevelService;


/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {

    protected Agent agent;                  // agent to perform the actions
    protected HLAction action;              // original action
    private HTNNode initialNode;          // list of all possible plans to solve the goal
    protected HTNState initialState;        // list of all possible plans to solve the goal
    protected HeuristicComparator aStarHeuristicComparator;  // heuristic used to compare nodes

    /*
    * Constructor: All a planner needs is the next goal and the agent solving it...
    * */
    public HTNPlanner(Agent agent, HLAction action) {
        //System.err.println("HTN Planner initializing.");
        this.agent = agent;
        this.action = action;
        this.aStarHeuristicComparator = new AStarHeuristicComparator(Main.heuristicMeasure);
        Position agentPosition = LevelService.getInstance().getPosition(agent);
        Position boxPosition = (action.getBox()!=null) ? LevelService.getInstance().getPosition(action.getBox()) : null;
        initialState = new HTNState( agentPosition, boxPosition );
//        System.err.println("is" + initialState.toString());
//        System.err.println( ((action==null) ? "action is null" : action.toString())  );
        this.initialNode = new HTNNode(initialState, action);
//        System.err.println(initialNode.toString());
    }

    /*
    * Returns the best guess for the number of actions used to solve the goal
    */
    public int getBestPlanApproximation() {
        return aStarHeuristicComparator.h( initialNode );
    }

    /*
    * is used to find the next plan (using the next box in line)
    */
    public PrimitivePlan plan() {
        System.err.println("HTNPlanner.plan(): " + initialNode.toString());
        return rePlan(initialNode);
    }

    public PrimitivePlan rePlan(HTNNode node) {
        System.err.println("HTNPlanner.rePlan(): initialnode" + node.toString());

        Strategy strategy = new BestFirstStrategy(aStarHeuristicComparator);

        System.err.format("HTN plan starting with strategy %s\n", strategy);
        strategy.addToFrontier(node);

        int iterations = 0;
        while (true) {
            if (iterations % Main.printIterations == 0 ) {
                System.err.println("rePlan():" + strategy.status());
            }

            if (strategy.frontierIsEmpty()) {
                System.err.format("Frontier is empty, HTNPlanner failed to create a plan!\n");
                //System.err.println(strategy.status());
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();
            System.err.println(leafNode.toString());
            //System.err.println(strategy.status());

            if (strategy.isExplored(leafNode.getState())) {
                // reject nodes resulting in states visited already
                if (leafNode.getConcreteAction() instanceof NoConcreteAction) { // check for progression
                    HTNNode n;
                    boolean noProgression = true;
                    for (int i = 0; i < 5; i++) {
                        n = leafNode.getParent(i);
                        if (n == null) {
                            noProgression = false;
                            break;
                        }
                        noProgression &= (n.getConcreteAction() instanceof NoConcreteAction);
//                        System.err.println(Boolean.toString(noProgression));
                    }
                    if (noProgression) {
//                        System.err.println("No progress for 5 nodes! skipping this node");
                        continue;
                    }
                } else {
//                    System.err.println("Effect already explored! skipping this node");
                    continue;
                }
//                System.err.println("Effect already explored, but NoActions, so still interesting!");
            }

            if (action.isPurposeFulfilled(leafNode.getState())) {
//                System.err.println(strategy.status());
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getState());
//            System.err.println("replan(): Adding node to explored");

            for (HTNNode n : leafNode.getRefinementNodes()) {
                System.err.println("replan(): Adding refinement node:" + n.toString());
                strategy.addToFrontier(n);
            }

            iterations++;
        }
    }

    public HLAction getBestIntention() {
        return action;
    }

    public Belief getBestBelief() {
        SolveGoalAction sga = (SolveGoalAction) initialNode.getRemainingPlan().getFirst();
        Belief belief = new Belief(agent);
        belief.setCurrentTargetBox(sga.getBox().getLabel());
        return belief;
    }
}

