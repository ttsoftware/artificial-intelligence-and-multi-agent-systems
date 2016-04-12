package dtu.agency.planners.htn;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalSuperAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.services.BDIService;
import dtu.agency.services.GlobalLevelService;

import java.util.PriorityQueue;

/**
 * This Planner uses the Hierarchical Task Network heuristic to subdivide high level tasks into primitive actions
 */
public class HTNGoalPlanner extends HTNPlanner {

    PriorityQueue<HTNNode> allInitialNodes;        // list of all possible plans to solve the goal
    /**
     * Constructor: All a planner needs is the next goal and the agent solving it...
     */
    public HTNGoalPlanner(Goal target) {
        super(new SolveGoalSuperAction(target), RelaxationMode.NoAgentsNoBoxes);
        debug("HTNGoalPlanner initializing.",2);
        this.allInitialNodes = createAllNodes(target, this.aStarHeuristicComparator);
        this.initialNode = allInitialNodes.peek();
        this.action = initialNode.getIntention();
        debug("Nodes: " + allInitialNodes.toString(),-2);
    }

    public PriorityQueue<HTNNode> getAllInitialNodes() {
        return new PriorityQueue<>(allInitialNodes);
    }

    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     */
    private PriorityQueue<HTNNode> createAllNodes(Goal target, HeuristicComparator heuristicComparator) {
        debug("HTNGoalPlanner.createAllNodes(): ", 2);
        PriorityQueue<HTNNode> allNodes = new PriorityQueue<>(heuristicComparator);

        for (Box box : GlobalLevelService.getInstance().getLevel().getBoxes()) {
            if (box.getLabel().toLowerCase().equals(target.getLabel().toLowerCase())) {
                HTNState initialState = new HTNState(
                        // TODO: agent intentional destination!! :-)
                        GlobalLevelService.getInstance().getPosition(BDIService.getInstance().getAgent()),
                        // TODO: agents believed position of box :-)
                        GlobalLevelService.getInstance().getPosition(box),
                        RelaxationMode.NoAgentsNoBoxes
                );
                HLAction initialAction = new SolveGoalAction(box, target);
                allNodes.offer(new HTNNode(initialState, initialAction));
            }
        }
        debug("Nodes created: \n" + String.join("\n", allNodes.toString()) , -2);
        return allNodes;
    }

    @Override
    public void setRelaxationMode(RelaxationMode mode) {
        super.setRelaxationMode(mode);
        for (HTNNode node : allInitialNodes) {
            node.getState().setRelaxationMode(mode);
        }
    }

    /**
     * is used to find the plan, by in turn planning for the best node (by heuristic)
     * until a plan (or none is found)
     */
    @Override
    public PrimitivePlan plan() {
        debug("HTNGoalPlanner.plan(): size of allinitialnodes:" + allInitialNodes.size(), 2);
        PriorityQueue<HTNNode> allNodes = getAllInitialNodes();
        PrimitivePlan plan = null;
        do {
            initialNode = allNodes.peek();
            action = initialNode.getIntention();
            debug("HTNGoalPlanner.rePlan() on " +initialNode.toString());
            plan = rePlan(initialNode);
            if (!(plan == null)) {
                debug("Plan found: " + plan.toString(), -2);
                return plan;
            } else {
                allNodes.remove();
            }
        } while (allNodes.size() > 0);
        debug("Failed to find a plan", -2);
        return null;
    }

}

