package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.Action;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
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
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }

    private final Agent agent = BDIService.getInstance().getAgent();
    private final HLAction originalAction;      // original Action
    private final PlanningLevelService pls;     // LevelService
    private final HTNNode initialNode;          // list of all possible plans to solve the goal
    private final HTNNodeComparator aStarHTNNodeComparator;  // heuristic used to compare nodes
    private Position agentDestination;

    /**
     * copy constructor
     * @param other HTNPlanner to be copied
     */
    public HTNPlanner(HTNPlanner other) {
        this.pls = new PlanningLevelService(other.pls);
        this.initialNode = new HTNNode(other.getInitialNode());
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(pls);
        this.originalAction = other.getIntention();
        this.agentDestination = other.getAgentDestination();
    }

    /**
     * Constructor: All a planner needs is the the agent and the original action to perform
     * @param pls The agents beliefs before planning
     * @param originalAction The High Level Action to be planned for
     * @param mode The Relaxation mode to plan in
     */
    public HTNPlanner(PlanningLevelService pls, HLAction originalAction, RelaxationMode mode) {
        debug("HTN Planner initializing:",2);
        this.originalAction = originalAction;
        this.pls = pls;
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(pls);
        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position boxPosition = agentPosition;
        if (originalAction.getBox()!=null) {
            boxPosition = pls.getPosition(originalAction.getBox());
        }
        HTNState initialState = new HTNState( agentPosition, boxPosition, pls, mode );
        debug("initial" + initialState.toString());
        this.initialNode = new HTNNode(initialState, originalAction, pls);
        debug("initial" + initialNode.toString());
        this.agentDestination = null;
        debug(initialNode.toString(),-2);
    }

    /**
     * Constructor:
     * @param pls The agents beliefs before planning
     * @param solveGoalAction an overall solvegoal action
     * @param mode the Relaxation mode to plan in
     */
    public HTNPlanner(PlanningLevelService pls, SolveGoalAction solveGoalAction, RelaxationMode mode) {
        debug("HTN Planner initializing:",2);
        this.originalAction = new HMoveBoxAction(
                solveGoalAction.getBox(),
                solveGoalAction.getBoxDestination(),
                solveGoalAction.getAgentDestination(pls)
        );
//        this.targetBox = solveGoalAction.getBox();
        this.pls = pls;
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(pls);
        Position agentPosition = pls.getPosition(BDIService.getInstance().getAgent());
        Position boxPosition = agentPosition;
        if (solveGoalAction.getBox()!=null) {
            boxPosition = pls.getPosition(solveGoalAction.getBox());
        }
        HTNState initialState = new HTNState( agentPosition, boxPosition, pls, mode );
        debug("initial" + initialState.toString());
        this.initialNode = new HTNNode(initialState, this.originalAction, pls);
        debug("initial" + initialNode);
        this.agentDestination = null;
        debug(initialNode.toString(),-2);
    }



    private HTNNode getInitialNode() {
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
        debug("HTNPlanner.plan():",2);

        debug("initial" + initialNode.toString());

        // PlanningLevelService assuming responsibility over agent and current box
        pls.startTracking(originalAction.getBox());

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
                pls.stopTracking();
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
                if (DebugService.inDebugMode()) { // all debugging information
                    Position pa = leafNode.getState().getAgentPosition();
                    Position pb = leafNode.getState().getBoxPosition();
                    String s = "HTNPlanner.plan() sucess, inserting agent and box into pls\n";
                    s += "Agent:" + pa + " Box:" + pb;
                    s += "\nWhat is at positions now:" + pls.getLevel().getBoardObjects()[pa.getRow()][pa.getColumn()];
                    s += " " + pls.getLevel().getBoardObjects()[pb.getRow()][pb.getColumn()];
                    debug(s);
                }
                // Succeeding, return responsibility of agent and box to pls.
                debug("htnplan succeeding with");
                debug("box: " + originalAction.getBox() + " @" + pls.getPosition(originalAction.getBox()));
                agentDestination = leafNode.getState().getAgentPosition();
                pls.stopTracking();
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
        String s = "HTNPlanner of agent " + agent;
        s += " performing " + ((this.originalAction !=null) ? this.originalAction.toString() : "null!");
        s += " with the next node \n" + this.initialNode.toString();
        return s;
    }

    public boolean hasPlanned() {
        return (agentDestination != null);
    }

    /**
     * commits the effect of this plan to the pls (PlanningLevelService) of this HTNPlanner
     */
    public void commitPlan(){
        debug("commit htnPlan into pls:", 2);
        Box targetBox = originalAction.getBox();
        Action action;
        if (targetBox==null) {
            action = new RGotoAction(agentDestination);
        } else {
            Position boxDestination = originalAction.getBoxDestination();
            action = new HMoveBoxAction(targetBox, boxDestination, agentDestination);
        }
        pls.apply(action);
        debug("htnPlan committed", -2);

    }

    public Position getAgentDestination() {
        return agentDestination;
    }
}

