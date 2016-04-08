package dtu.agency.planners.htn;

import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalSuperAction;
import dtu.agency.agent.bdi.Belief;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.services.GlobalLevelService;

import java.util.PriorityQueue;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNGoalPlanner extends HTNPlanner {

    PriorityQueue<HTNNode> allInitialNodes;        // list of all possible plans to solve the goal
    /*
    * Constructor: All a planner needs is the next goal and the agent solving it...
    * */
    public HTNGoalPlanner(Agent agent, Goal target) {
        super(agent, new SolveGoalSuperAction(target));
//        System.err.println("HTN Planner initializing.");
        this.allInitialNodes = createAllNodes(target, this.aStarHeuristicComparator);
//        System.err.println("Nodes: " + allInitialNodes.toString());
    }

    /*
    *  Fills the data structure containing information on ways to solve this particular target
    */
    private PriorityQueue<HTNNode> createAllNodes(Goal target, HeuristicComparator heuristicComparator) {
        //System.err.println("CreateAllNodes: ");
        PriorityQueue<HTNNode> allNodes = new PriorityQueue<>(heuristicComparator);

        for (Box box : GlobalLevelService.getInstance().getLevel().getBoxes()) {
            if (box.getLabel().toLowerCase().equals(target.getLabel().toLowerCase())) {
                HTNState initialState = new HTNState(
                        GlobalLevelService.getInstance().getPosition(this.agent),
                        GlobalLevelService.getInstance().getPosition(box)
                );
                HLAction initialAction = new SolveGoalAction(box, target);
                allNodes.offer(new HTNNode(initialState, initialAction));
            }
        }
        return allNodes;
    }

    /*
    * is used to find the next plan (using the next box in line)
    */
    @Override
    public PrimitivePlan plan() {
//        System.err.println("HTNGoalPlanner.plan(): size of allinitialnodes:" + allInitialNodes.size());
        PrimitivePlan plan = null;
        HTNNode node;
        do {
            node = allInitialNodes.poll();
//            System.err.println("HTNGoalPlanner.plan():");
            plan = rePlan(node);
            if (!(plan == null)) {
//                System.err.println("HTNGoalPlanner.plan(): " + plan.toString());
                return plan;
            }
        } while (allInitialNodes.size() > 0);
//        System.err.println("plan is null :-(");
        return null;
    }

    @Override
    public HLAction getBestIntention() {
        return (HLAction) allInitialNodes.peek().getRemainingPlan().getFirst();
    }

    @Override
    public Belief getBestBelief() {
        HTNNode node = allInitialNodes.peek();
        SolveGoalAction sga = (SolveGoalAction) node.getRemainingPlan().getFirst();
        Belief belief = new Belief(agent);
        belief.setCurrentTargetBox(sga.getBox().getLabel());
        return belief;
    }
}

