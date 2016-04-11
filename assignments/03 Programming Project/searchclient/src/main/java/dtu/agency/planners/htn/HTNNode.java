package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.SolveGoalAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.planners.htn.heuristic.AStarHeuristicComparator;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;
import dtu.agency.services.DebugService;
import dtu.agency.services.GlobalLevelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HTNNode {
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }

    private static Random rnd = new Random(1);

    private HTNNode parent;
    private ConcreteAction concreteAction;   // primitive concreteAction represented by this node
    private HTNState state;                  // status of the relevant board features after applying the concreteAction of this node
    private MixedPlan remainingPlan;         // list of successive (abstract) actions
    private int generation;                  // generation - how many ancestors exist? -> how many moves have i performed
//    private RelaxationMode r =  {WALL | NOAGENTS | FULL} // could introduce relaxation levels here in htn node

    public HTNNode(HTNNode parent, ConcreteAction concreteAction, HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.concreteAction = concreteAction;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        if (parent == null) {
            this.generation = 0;
        } else {
            this.generation = ((concreteAction==null) || (concreteAction instanceof NoConcreteAction)) ? parent.generation : (parent.generation + 1);
        }
    }

    public HTNNode(HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = null;
        this.concreteAction = null;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        this.generation = 0;
    }

    public HTNNode(HTNState initialEffects, HLAction highLevelAction) {
        this.parent = null;
        this.concreteAction = null;
        this.state = initialEffects;
        this.remainingPlan = new MixedPlan();
        this.remainingPlan.addAction(highLevelAction);
        this.generation = 0;
    }

    /**
     * getters and setters section
     */
    public int getGeneration() {
        return generation;
    }

    public boolean isInitialNode() {
        return this.parent == null;
    }

    public HTNNode getParent() {
        return parent;
    }

    public HTNNode getParent(int generation) {
        HTNNode n = this;
        for (int i = 0 ; i < generation ; i++) {
            n = (n.isInitialNode()) ? null : n.getParent();
        }
        return n;
    }

    public ConcreteAction getConcreteAction() {
        return concreteAction;
    }

    public HTNState getState() {
        return state;
    }

    public MixedPlan getRemainingPlan() {
        return remainingPlan;
    }

    /**
     * branching of this node into nodes of possible actions from this state
     * @return
     */
    public ArrayList<HTNNode> getRefinementNodes() {
        debug("HTNNode.getRefinements()",2);
        ArrayList<HTNNode> refinementNodes = new ArrayList<>();

        if (this.remainingPlan.isEmpty()) {
            debug("No more remaining actions, returning empty list of refinement nodes", -2);
            return refinementNodes;
        }

        Action nextAction = remainingPlan.removeFirst();

        if (nextAction instanceof ConcreteAction) { // case the concreteAction is primitive, add it as only node,
            debug("Next concreteAction is Primitive, thus a single ChildNode is created");
            ConcreteAction primitive = (ConcreteAction) nextAction;
            HTNNode only = childNode( primitive, this.remainingPlan);
            if (only != null) { refinementNodes.add(only);}
            debug("Refinement: " + refinementNodes.toString(), -2);
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the concreteAction is high level, get the refinements from the concreteAction
            debug("Next concreteAction is High Level, thus a we seek refinements to it:");
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refinements = this.getState().getRefinements(highLevelAction);
            debug("HTNNode.getRefinements() got: " + refinements.toString());

            for (MixedPlan refinement : refinements) {

                ConcreteAction first = null; // remains null iff first concreteAction is abstract

                if (refinement.getFirst() instanceof ConcreteAction) {
                    first = (ConcreteAction) refinement.removeFirst();
                }
                refinement.extend(remainingPlan);
                HTNNode nextNode = childNode(first, refinement);

                if (nextNode != null) {
                    refinementNodes.add(nextNode);
                }
            }
        }

        Collections.shuffle(refinementNodes, rnd);
        debug("Refinements: " + refinementNodes.toString(), -2);
        return refinementNodes;
    }

    /**
     * creates a new child node of this node
     * @param primitiveConcreteAction
     * @param remainingActions
     * @return
     */
    private HTNNode childNode(ConcreteAction primitiveConcreteAction, MixedPlan remainingActions) {
        HTNState oldState = this.getState();
        HTNState newState = (primitiveConcreteAction ==null) ? oldState : oldState.applyConcreteAction(primitiveConcreteAction);
        if (newState.getBoxPosition()==null) {
            HLAction nextHLA = (HLAction) remainingActions.getFirst();
            if (nextHLA.getType()== AbstractActionType.SolveGoal) {
                SolveGoalAction sga = (SolveGoalAction) nextHLA;
                newState = new HTNState(
                        newState.getAgentPosition(),
                        GlobalLevelService.getInstance().getPosition(sga.getBox()), // TODO: GlobalLevelService
                        oldState.getRelaxationMode()
                );
            }
        }
        primitiveConcreteAction = (primitiveConcreteAction ==null) ? new NoConcreteAction() : primitiveConcreteAction;
        return new HTNNode(this, primitiveConcreteAction, newState, remainingActions);
    }

    /**
     * find the path up and to the original ancestor, collecting all the concrete actions
     * to a fine concrete plan, excluding 'null' and 'NoOp' actions.
     * @return concrete plan of primitive actions
     */
    public PrimitivePlan extractPlan() {
        PrimitivePlan plan = new PrimitivePlan();
        HTNNode node = this;
        ConcreteAction previous;
        while (!node.isInitialNode()) {
            previous = node.getConcreteAction();
            if ((previous != null) && !(previous instanceof NoConcreteAction)) plan.pushAction(previous);
            node = node.getParent();
        }
        return plan;
    }

    /**
     * Retrieves the intention of the original ancestor of this node
     * @return intention in form of highLevelAction
     */
    public HLAction getIntention() {
        HTNNode node = this;
        debug(node.toString());
        debug(node.getRemainingPlan().getActions().toString());
        while (!node.isInitialNode()) {
            node = this.parent;
        }
        debug(node.toString());
        debug(node.getRemainingPlan().getActions().toString());
        // getting initial High level action
        Action action;
        if (node.getRemainingPlan().getActions().size()==1) {
            action = node.getRemainingPlan().getActions().getFirst();
        } else {
            debug("initial action is not a single action?");
            debug(node.getRemainingPlan().getActions().toString());
            return null;
        }
        if (action instanceof HLAction) {
            HLAction hlAction = (HLAction) action;
            return hlAction;
        } else {
            return null;
        }
    }

/*
    */
/**
     * Used to compare and to enable for key usage in hashmaps
     * @return
     *//*

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parent.hashCode();
        result = prime * result + concreteAction.hashCode();
        result = prime * result + state.hashCode();
        result = prime * result + remainingPlan.hashCode();
        return result;
    }

    */
/**
     * Comparing nodes to other nodes...
     * used in strategic discarding of similar nodes
     *//*

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNNode other = (HTNNode) obj;
        if (parent != other.parent)
            return false;
        if (concreteAction != other.concreteAction)
            return false;
        if (state != other.state)
            return false;
        if (!remainingPlan.equals(other.remainingPlan)) {
            return false;
        }
        return true;
    }
*/

    @Override
    public String toString() {
        HeuristicComparator h = new AStarHeuristicComparator(Main.heuristicMeasure);
        StringBuilder s = new StringBuilder();
        s.append("HTNNode: {");
        s.append("Generation: " + Integer.toString(this.generation));
        s.append(", HeuristicComparator: " + Integer.toString(h.h(this)));
        s.append(", ConcreteAction: " + ((concreteAction !=null) ? concreteAction.toString() : "null") );
        s.append(", State: " + this.state.toString() + ",\n");
        s.append("          RemainingActions: " + this.remainingPlan.toString() + "}" );
        return s.toString();
    }


}