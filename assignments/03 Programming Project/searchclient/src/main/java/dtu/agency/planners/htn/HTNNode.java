package dtu.agency.planners.htn;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.planners.htn.heuristic.AStarHTNNodeComparator;
import dtu.agency.planners.htn.heuristic.HTNNodeComparator;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * Node for building a graph using the Hierarchical Task Network method
 * Used by HTNPlanner
 */
public class HTNNode {

    private final HTNNode parent;
    private final ConcreteAction concreteAction;   // primitive concreteAction represented by this node
    private final HTNState state;                  // status of the relevant board features after applying the concreteAction of this node
    private final MixedPlan remainingPlan;         // list of successive (abstract) actions
    private final int generation;                  // generation - how many ancestors exist? -> how many moves have i performed
    private final HTNNodeComparator nodeComparator;

    /**
     * Copy constructor
     *
     * @param other Node to be copied
     */
    public HTNNode(HTNNode other) {
        this.parent = other.parent;
        this.generation = other.getGeneration();
        this.concreteAction = other.getConcreteAction();
        this.state = new HTNState(other.getState());
        this.remainingPlan = new MixedPlan(other.remainingPlan);
        this.nodeComparator = other.nodeComparator;
    }

    /**
     * The constructor used to create all child nodes in the planning hierarchy
     *
     * @param parent
     * @param concreteAction
     * @param initialEffects
     * @param highLevelPlan
     */
    public HTNNode(HTNNode parent, ConcreteAction concreteAction, HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.concreteAction = concreteAction;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        this.generation = ((concreteAction == null) || (concreteAction instanceof NoConcreteAction)) ? parent.generation : (parent.generation + 1);
        this.nodeComparator = parent.nodeComparator;
    }

    /**
     * The Constructor used to create an initial node in a planning tree
     *
     * @param initialState    The positions of box and agent prior to planning
     * @param highLevelAction The initial High Level Action
     * @param pls             The PlanningLevelService used for this planner
     */
    public HTNNode(HTNState initialState, HLAction highLevelAction, PlanningLevelService pls) {
        this.parent = null;
        this.concreteAction = null;
        this.state = initialState;
        this.remainingPlan = new MixedPlan();
        this.remainingPlan.addAction(highLevelAction);
        this.generation = 0;
        this.nodeComparator = new AStarHTNNodeComparator(pls);
    }

    /**
     * getters and setters section
     */
    public int getGeneration() {
        return generation;
    }

    private boolean isInitialNode() {
        return this.parent == null;
    }

    private HTNNode getParent() {
        return new HTNNode(parent);
    }

    public HTNNode getParent(int generation) {
        HTNNode n = this;
        for (int i = 0; i < generation; i++) {
            n = (n.isInitialNode()) ? null : n.getParent();
        }
        return n;
    }

    public ConcreteAction getConcreteAction() {
        if (this.concreteAction == null) {
            return null;
        } else {
            return ConcreteAction.cloneConcreteAction(this.concreteAction);
        }
    }

    public HTNState getState() {
        return new HTNState(state);
    }

    public MixedPlan getRemainingPlan() {
        return new MixedPlan(remainingPlan);
    }

    /**
     * The refinements are a list of branching children into nodes of possible actions from this node/state
     *
     * @return an ArryList of refinement HTNNodes
     */
    public ArrayList<HTNNode> getRefinementNodes() {
        ArrayList<HTNNode> refinementNodes = new ArrayList<>();

        if (getRemainingPlan().isEmpty()) {
            return refinementNodes;
        }

        MixedPlan followingActions = getRemainingPlan();
        Action nextAction = followingActions.removeFirst();

        if (nextAction instanceof ConcreteAction) {
            // case the concreteAction is primitive, add it as only node,
            ConcreteAction primitive = (ConcreteAction) nextAction;
            HTNNode only = childNode(primitive, followingActions);
            if (only != null) {
                refinementNodes.add(only);
            }
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the concreteAction is high level, get the refinements from the concreteAction
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refinements = this.getState().getRefinements(highLevelAction);

            for (MixedPlan refinement : refinements) {

                ConcreteAction first = null; // remains null iff first concreteAction is abstract

                if (refinement.getFirst() instanceof ConcreteAction) {
                    first = (ConcreteAction) refinement.removeFirst();
                }
                refinement.extend(followingActions);
                HTNNode nextNode = childNode(first, refinement);

                if (nextNode != null) {
                    refinementNodes.add(nextNode);
                }
            }
        }

        return refinementNodes;
    }

    /**
     * Creates a new child node of this node
     *
     * @param primitiveConcreteAction push, pull, or move concrete action
     * @param remainingActions        List of remaining actions
     * @return A HTNNode which is child of current node
     */
    private HTNNode childNode(ConcreteAction primitiveConcreteAction, MixedPlan remainingActions) {
        HTNState oldState = this.getState();
        HTNState newState = (primitiveConcreteAction == null) ? oldState : oldState.applyConcreteAction(primitiveConcreteAction);
        if (newState.getBoxPosition() == null) {
            HLAction nextHLA = (HLAction) remainingActions.getFirst();
        }
        primitiveConcreteAction = (primitiveConcreteAction == null) ? new NoConcreteAction() : primitiveConcreteAction;
        return new HTNNode(this, primitiveConcreteAction, newState, remainingActions);
    }

    /**
     * Finds the path up and to the original ancestor, collecting all the concrete actions
     * to a fine concrete plan, excluding 'null' and 'NoOp' actions.
     *
     * @return Primitive plan of concrete actions
     */
    public PrimitivePlan extractPlan() {
        PrimitivePlan plan = new PrimitivePlan();
        HTNNode node = this;
        ConcreteAction previous;
        while (!node.isInitialNode()) {
            previous = node.getConcreteAction();
            if ((previous != null) && !(previous instanceof NoConcreteAction))
                plan.pushAction(previous);
            node = node.getParent();
        }
        return plan;
    }

    @Override
    public String toString() {
        String s = "HTNNode: {Generation: " + Integer.toString(this.generation);
        s += ", HTNNodeComparator: " + Integer.toString(nodeComparator.h(this));
        s += ", ConcreteAction: " + ((concreteAction != null) ? concreteAction.toString() : "null");
        s += ", State: " + this.state.toString() + ",\n";
        s += "          RemainingActions: " + this.remainingPlan.toString() + "}";
        return s;
    }
}