package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.htn.heuristic.AStarHTNNodeComparator;
import dtu.agency.planners.htn.heuristic.HTNNodeComparator;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;


/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {
    protected static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    protected static void debug(String msg){ debug(msg, 0); }

    private HLAction originalAction;      // original Action
    private PlanningLevelService pls;     // LevelService
    private HTNNode initialNode;          // list of all possible plans to solve the goal
    private HTNNodeComparator aStarHTNNodeComparator;  // heuristic used to compare nodes

    public HTNPlanner(HTNPlanner other) {
        this.pls = new PlanningLevelService(other.pls);
        this.initialNode = new HTNNode(other.getInitialNode());
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(Main.heuristicMeasure);
        this.originalAction = other.getIntention();
    }
        /**
        * Constructor: All a planner needs is the the agent and the original action to perform
        * */
    public HTNPlanner(PlanningLevelService pls, HLAction originalAction, RelaxationMode mode) {
        debug("HTN Planner initializing:",2);
        this.originalAction = originalAction;
        this.pls = pls;
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(Main.heuristicMeasure);
        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position boxPosition = agentPosition;
        if (originalAction.getBox()!=null) {
            boxPosition = pls.getPosition(originalAction.getBox());
        }
        HTNState initialState = new HTNState( agentPosition, boxPosition, pls, mode );
        debug("initial" + initialState.toString());
        debug( ((originalAction ==null) ? "HLAction is null" : originalAction.toString())  );
        this.initialNode = new HTNNode(initialState, originalAction);
        debug(initialNode.toString(),-2);
    }

    public HTNNode getInitialNode() {
        return new HTNNode(initialNode);
    }

    public RelaxationMode getRelaxationMode() {
        return initialNode.getState().getRelaxationMode();
    }

    public void setRelaxationMode(RelaxationMode mode) {
        initialNode.getState().setRelaxationMode(mode);
    }

    /**
     * Returns the intention to be solved by this planner
     */
    public HLAction getIntention() {
        return HLAction.getOriginalAction(originalAction);
    }

    /**
    * Returns the best guess for the number of actions used to solve the goal
    */
    public int getBestPlanApproximation() {
        return aStarHTNNodeComparator.h( initialNode );
    }

    /**
     * Finds the concrete plan (provided an (initial) node)
     * This is the actual graph building phase in HTNPlanner
     */
    public PrimitivePlan plan() {
        debug("HTNPlanner.plan():",2);originalAction.getBoxDestination();
        debug("initial" + initialNode.toString());

        // update PlanningLevelService, assuming responsibility over agent and current box
        Position agentOrigin = pls.getAgentPosition();
        pls.removeAgent(BDIService.getInstance().getAgent());
        if (originalAction.getBox()!=null) {
            pls.setCurrentBox(originalAction.getBox());
//            boxOrigin = pls.getPosition(originalAction.getBox());
//            pls.removeBox(originalAction.getBox());
        }
//        System.err.println("htn planning: box:" +originalAction.getBox()+ " " + boxOrigin);


        Strategy strategy = new BestFirstStrategy(aStarHTNNodeComparator);

        debug("HTN plan starting with strategy " + strategy.toString() + "\n");
        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while (true) {
            if (iterations % Main.printIterations == 0 ) {
                debug( strategy.status() );
            }

            if (strategy.frontierIsEmpty()) {
                debug("Frontier is empty, HTNPlanner failed to create a plan!\n");
                debug(strategy.status(), -2);
                // Failing, return responsibility of agent and box to pls.
                System.err.println("Frontier is empty, HTNPlanner failed to create a plan!\n");
                pls.insertAgent(BDIService.getInstance().getAgent(), agentOrigin);
//                if (originalAction.getBox()!=null) { // reinsert box at its original position
//                    pls.insertBox(originalAction.getBox(), boxOrigin);
//                }
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

            if (leafNode.getState().isPurposeFulfilled( getIntention() )) {
                debug(strategy.status(), -2);
                Position pa = leafNode.getState().getAgentPosition();
                Position pb = leafNode.getState().getBoxPosition();
                String s = "sucess, inserting agent and box into pls\n";
                s += "Agent:" + pa + " Box:" + pb;
                s += "\nAt positions:" + pls.getLevel().getBoardObjects()[pa.getRow()][pa.getColumn()];
                s += pls.getLevel().getBoardObjects()[pb.getRow()][pb.getColumn()];
                System.err.println(s);
                // Succeeding, return responsibility of agent and box to pls.
                if (originalAction.getBox()!=null) {
                    pls.removeBox(originalAction.getBox());
                    pls.insertBox(originalAction.getBox(), leafNode.getState().getBoxPosition());
                }
                pls.insertAgent(BDIService.getInstance().getAgent(), leafNode.getState().getAgentPosition());
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

    @Override
    public String toString(){
        String s = "HTNPlanner of agent " + BDIService.getInstance().getAgent().toString();
        s += " performing " + ((this.originalAction !=null) ? this.originalAction.toString() : "null!");
        s += " with the next node \n" + this.initialNode.toString();
        return s;
    }

}

