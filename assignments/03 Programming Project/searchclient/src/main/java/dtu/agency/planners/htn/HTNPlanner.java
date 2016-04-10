package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalSuperAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.*;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristicComparator;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.services.DebugService;
import dtu.agency.services.GlobalLevelService;


/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

    protected Agent agent;                  // agent to perform the actions
    protected HLAction action;              // original action
    private HTNNode initialNode;          // list of all possible plans to solve the goal
    protected HTNState initialState;        // list of all possible plans to solve the goal
    protected HeuristicComparator aStarHeuristicComparator;  // heuristic used to compare nodes

    /*
    * Constructor: All a planner needs is the next goal and the agent solving it...
    * */
    public HTNPlanner(Agent agent, HLAction action) {
        debug("HTN Planner initializing:",2);
        this.agent = agent;
        this.action = action;
        this.aStarHeuristicComparator = new AStarHeuristicComparator(Main.heuristicMeasure);
        Position agentPosition = GlobalLevelService.getInstance().getPosition(agent);
        Position boxPosition = (action.getBox()!=null) ? GlobalLevelService.getInstance().getPosition(action.getBox()) : null;
        initialState = new HTNState( agentPosition, boxPosition );
        debug("initial" + initialState.toString());
        debug( ((action==null) ? "HLAction is null" : action.toString())  );
        this.initialNode = new HTNNode(initialState, action);
        debug(initialNode.toString(),-2);
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
        debug("HTNPlanner.plan():",2);
        debug(initialNode.toString(),-2);
        return rePlan(initialNode);
    }

    public PrimitivePlan rePlan(HTNNode node) {
        debug("HTNPlanner.rePlan():",2);
        debug("initial" + node.toString());

        Strategy strategy = new BestFirstStrategy(aStarHeuristicComparator);

        debug("HTN plan starting with strategy " + strategy.toString() + "\n");
        strategy.addToFrontier(node);

        int iterations = 0;
        while (true) {
            if (iterations % Main.printIterations == 0 ) {
                debug( strategy.status() );
            }

            if (strategy.frontierIsEmpty()) {
                debug("Frontier is empty, HTNPlanner failed to create a plan!\n");
                debug(strategy.status(), -2);
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();
            debug(leafNode.toString());
            debug(strategy.status());

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
                    }
                    if (noProgression) {
                        debug("No progress for 5 nodes! skipping this node");
                        continue;
                    }
                } else {
                    debug("Effect already explored! skipping this node");
                    continue;
                }
                debug("Effect already explored, but NoActions, so still interesting!");
            }

            if (leafNode.getState().isPurposeFulfilled(action)) {
                debug(strategy.status(), -2);
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getState());
            debug("Adding node to explored");

            for (HTNNode n : leafNode.getRefinementNodes()) {
                debug("Adding refinement node:",2);
                debug(n.toString(),-2);
                strategy.addToFrontier(n);
            }

            iterations++;
        }
    }

    public HLAction getIntention() {
        return action;
    }

/*
    public AgentBelief getBestBelief() {
        SolveGoalAction sga = (SolveGoalAction) initialNode.getRemainingPlan().getFirst();
        AgentBelief belief = new AgentBelief(agent);
        belief.setCurrentTargetBox(sga.getBox().getLabel());
        return belief;
    }
*/
}

