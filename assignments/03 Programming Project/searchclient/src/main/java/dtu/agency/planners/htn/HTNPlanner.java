package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.board.*;
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
    protected HTNNode initialNode;          // list of all possible plans to solve the goal
    protected HeuristicComparator aStarHeuristicComparator;  // heuristic used to compare nodes

    public HTNPlanner(HTNPlanner other) {
        this.agent = new Agent(other.agent);
        this.initialNode = new HTNNode(other.getInitialNode());
        this.aStarHeuristicComparator = new AStarHeuristicComparator(Main.heuristicMeasure);
        this.action = other.getAction();
    }
        /**
        * Constructor: All a planner needs is the the agent and the action to perform
        * */
    public HTNPlanner(Agent agent, HLAction action, RelaxationMode mode) {
        debug("HTN Planner initializing:",2);
        this.agent = agent;
        this.action = action;
        this.aStarHeuristicComparator = new AStarHeuristicComparator(Main.heuristicMeasure);
        Position agentPosition = GlobalLevelService.getInstance().getPosition(agent);
        Position boxPosition = agentPosition;
        if (action.getBox()!=null) {
            boxPosition = GlobalLevelService.getInstance().getPosition(action.getBox());
        }
        HTNState initialState = new HTNState( agentPosition, boxPosition, mode );
        debug("initial" + initialState.toString());
        debug( ((action==null) ? "HLAction is null" : action.toString())  );
        this.initialNode = new HTNNode(initialState, action);
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
        return getAction();
    }

    /**
    * Returns the best guess for the number of actions used to solve the goal
    */
    public int getBestPlanApproximation() {
        return aStarHeuristicComparator.h( initialNode );
    }

    /**
    * Finds the concrete plan
    */
    public PrimitivePlan plan() {
        debug("HTNPlanner.plan():");
        return rePlan(initialNode);
    }

    /**
     * Finds the concrete plan (provided an (initial) node)
     * This is the actual graph building phase in HTNPlanner
     */
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

            if (leafNode.getState().isPurposeFulfilled( getAction() )) {
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

    @Override
    public String toString(){
        String s = "HTNPlanner of agent " +this.agent.toString();
        s += " performing " + ((this.action!=null) ? this.action.toString() : "null!");
        s += " with the next node \n" + this.initialNode.toString();
        return s;
    }

    public HLAction getAction() {
        switch (action.getType()) {
            case SolveGoal:
                SolveGoalAction sga = (SolveGoalAction) action;
                return new SolveGoalAction(sga);

            case Circumvent:
                CircumventBoxAction cba = (CircumventBoxAction) action;
                return new CircumventBoxAction(cba);

            case RGotoAction:
                RGotoAction gta = (RGotoAction) action;
                return new RGotoAction(gta);

            case MoveBoxAction:
                RMoveBoxAction rmba = (RMoveBoxAction) action;
                return new RMoveBoxAction(rmba);

            case SolveGoalSuper:
                SolveGoalSuperAction sgs = (SolveGoalSuperAction) action;
                return new SolveGoalSuperAction(sgs);

            case No:
                NoAction na = (NoAction) action;
                return new NoAction(na);

            case MoveBoxAndReturn:
                HMoveBoxAction hmba = (HMoveBoxAction) action;
                return new HMoveBoxAction(hmba);

            default:
                return null;
        }
    }

}

