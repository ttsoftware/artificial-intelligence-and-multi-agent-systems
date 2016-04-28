package dtu.agency.planners.htn;

import dtu.agency.actions.Action;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.heuristic.AStarHTNNodeComparator;
import dtu.agency.planners.htn.heuristic.HTNNodeComparator;
import dtu.agency.planners.htn.strategy.BestFirstStrategy;
import dtu.agency.planners.htn.strategy.Strategy;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.PlanningLevelService;


/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNPlanner {

    private final Agent agent = BDIService.getInstance().getAgent();
    private final PlanningLevelService pls;     // LevelService
    private final HTNNodeComparator aStarHTNNodeComparator;  // heuristic used to compare nodes
    private HLAction originalAction;      // original Action
    private HTNNode initialNode;          // list of all possible plans to solve the goal
    private Position agentDestination;

    /**
     * copy constructor
     *
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
     *
     * @param pls            The agents beliefs before planning
     * @param originalAction The High Level Action to be planned for
     * @param mode           The Relaxation mode to plan in
     */
    public HTNPlanner(PlanningLevelService pls, HLAction originalAction, RelaxationMode mode) {
        this.originalAction = originalAction;
        this.pls = pls;
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(pls);
        Position agentPosition = pls.getPosition(agent);
        Position boxPosition = agentPosition;
        if (originalAction.getBox() != null) {
            boxPosition = pls.getPosition(originalAction.getBox());
        }
        HTNState initialState = new HTNState(agentPosition, boxPosition, pls, mode);
        this.initialNode = new HTNNode(initialState, originalAction, pls);
        this.agentDestination = null;
    }

    /**
     * Constructor:
     *
     * @param pls             The agents beliefs before planning
     * @param solveGoalAction an overall solvegoal action
     * @param mode            the Relaxation mode to plan in
     */
    public HTNPlanner(PlanningLevelService pls, SolveGoalAction solveGoalAction, RelaxationMode mode) {
        this.originalAction = new HMoveBoxAction(
                solveGoalAction.getBox(),
                solveGoalAction.getBoxDestination(),
                solveGoalAction.getAgentDestination(pls)
        );
        this.pls = pls;
        this.aStarHTNNodeComparator = new AStarHTNNodeComparator(pls);
        Position agentPosition = pls.getPosition(agent);
        Position boxPosition = agentPosition;
        if (solveGoalAction.getBox() != null) {
            boxPosition = pls.getPosition(solveGoalAction.getBox());
        }
        HTNState initialState = new HTNState(agentPosition, boxPosition, pls, mode);
        this.initialNode = new HTNNode(initialState, this.originalAction, pls);
        this.agentDestination = null;
    }

    /**
     * Make the HTNPlanner ready to run again with new Action / Relaxation parameters
     *
     * @param solveGoalAction
     * @param mode
     */
    public void reload(SolveGoalAction solveGoalAction, RelaxationMode mode) {
        agentDestination = null;
        this.originalAction = new HMoveBoxAction(
                solveGoalAction.getBox(),
                solveGoalAction.getBoxDestination(),
                solveGoalAction.getAgentDestination(pls)
        );
        Position agentOrigin = pls.getPosition(agent);
        Position boxOrigin = agentOrigin;
        if (originalAction.getBox() != null) {
            boxOrigin = pls.getPosition(originalAction.getBox());
        }
        HTNState initialState = new HTNState(agentOrigin, boxOrigin, pls, mode);
        this.initialNode = new HTNNode(initialState, this.originalAction, pls);
    }

    /**
     * Make the HTNPlanner ready to run again with new Action / Relaxation parameters
     *
     * @param action
     * @param mode
     */
    public void reload(HLAction action, RelaxationMode mode) {
        agentDestination = null;
        originalAction = action;
        Position agentOrigin = pls.getPosition(agent);
        Position boxOrigin = agentOrigin;
        if (originalAction.getBox() != null) {
            boxOrigin = pls.getPosition(originalAction.getBox());
        }
        HTNState initialState = new HTNState(agentOrigin, boxOrigin, pls, mode);
        this.initialNode = new HTNNode(initialState, this.originalAction, pls);
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
        return HLAction.cloneHLAction(originalAction);
    }

    /**
     * Returns the best guess for the number of actions used to solve the goal
     */
    public int getBestPlanApproximation() {
        return aStarHTNNodeComparator.h(initialNode);
    }

    /**
     * Finds the concrete plan (provided an (initial) node)
     * This is the actual graph building phase in HTNPlanner
     */
    public PrimitivePlan plan() {
        // PlanningLevelService assuming responsibility over agent and current box
        System.err.println("" + originalAction.getBox());
        pls.startTracking(originalAction.getBox());

        Strategy strategy = new BestFirstStrategy(aStarHTNNodeComparator);

        strategy.addToFrontier(initialNode);

        int iterations = 0;
        while (true) {

            if (strategy.frontierIsEmpty()) {
                // Failing, return responsibility of agent and box to pls.
                pls.stopTracking();
                return null;
            }

            HTNNode leafNode = strategy.getAndRemoveLeaf();

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
                        // "No progress for 5 nodes! skipping this node
                        continue;
                    }
                } else {
                    // Effect already explored! skipping this node
                    continue;
                }
                // "Effect already explored, but NoActions, so still interesting!
            }

            if (leafNode.getState().isPurposeFulfilled(getIntention())) {
                agentDestination = leafNode.getState().getAgentPosition();
                pls.stopTracking();
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode.getState());

            leafNode.getRefinementNodes().forEach(strategy::addToFrontier);

            iterations++;
        }
    }

    @Override
    public String toString() {
        String s = "HTNPlanner of agent " + agent;
        s += " performing " + ((this.originalAction != null) ? this.originalAction.toString() : "null!");
        s += " with the next node \n" + this.initialNode.toString();
        return s;
    }

    public boolean hasPlanned() {
        return (agentDestination != null);
    }

    /**
     * commits the effect of this plan to the pls (PlanningLevelService) of this HTNPlanner
     */
    public void commitPlan() {
        if (agentDestination == null) throw new AssertionError("htn cannot commit as planning failed");
        Box targetBox = originalAction.getBox();
        Action action;
        if (targetBox == null) {
            action = new RGotoAction(agentDestination);
        } else {
            Position boxDestination = originalAction.getBoxDestination();
            action = new HMoveBoxAction(targetBox, boxDestination, agentDestination);
        }
        pls.apply(action);
    }

    public Position getAgentDestination() {
        return agentDestination;
    }
}

