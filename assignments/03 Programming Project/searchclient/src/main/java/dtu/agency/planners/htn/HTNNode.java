package dtu.agency.planners.htn;

import dtu.Main;
import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.concreteaction.NoConcreteAction;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.htn.heuristic.AStarHeuristicComparator;
import dtu.agency.planners.htn.heuristic.HeuristicComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class HTNNode {

    private static Random rnd = new Random(1);

    private HTNNode parent;
    private ConcreteAction concreteAction;   // primitive concreteAction represented by this node
    private HTNState state;  // status of the relevant board features after applying the concreteAction of this node
    private MixedPlan remainingPlan; // list of successive (abstract) actions
    private int generation; // generation - how many ancestors exist? -> how many moves have i performed
    // private Relaxation r =  {WALL | NOAGENTS | FULL} // could introduce relaxation levels here in htn node

    public HTNNode(HTNNode parent, ConcreteAction concreteAction, HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.concreteAction = concreteAction;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        this.generation = (parent == null) ? 0 : (parent.generation + 1);
    }

    public HTNNode(HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = null;
        this.concreteAction = null;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        this.generation = (parent == null) ? 0 : (parent.generation + 1);
    }

    public HTNNode(HTNState initialEffects, HLAction highLevelAction) {
        this.parent = null;
        this.concreteAction = null;
        this.state = initialEffects;
        MixedPlan plan = new MixedPlan();
        plan.addAction(highLevelAction);
        this.remainingPlan = plan;
        this.generation = (parent == null) ? 0 : (parent.generation + 1);
    }

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

    public void setParent(HTNNode parent) {
        this.parent = parent;
    }

    public ConcreteAction getConcreteAction() {
        return concreteAction;
    }

    public void setConcreteAction(ConcreteAction concreteAction) {
        this.concreteAction = concreteAction;
    }

    public HTNState getState() {
        return state;
    }

    public void setState(HTNState state) {
        this.state = state;
    }

    public MixedPlan getRemainingPlan() {
        return remainingPlan;
    }

    public ArrayList<HTNNode> getRefinementNodes() {
        //System.err.println("HTNNode: getting refinements");

        ArrayList<HTNNode> refinementNodes = new ArrayList<>();

        if (this.remainingPlan.isEmpty()) {
            System.err.println("No more remaining actions, returning empty list of refinement nodes");
            return refinementNodes;
        }

        Action nextAction = remainingPlan.removeFirst();

        if (nextAction instanceof ConcreteAction) { // case the concreteAction is primitive, add it as only node,
            //System.err.println("Next concreteAction is Primitive, thus a single ChildNode is created");
            ConcreteAction primitive = (ConcreteAction) nextAction;
            HTNNode only = childNode( primitive, this.remainingPlan); // is remainingPlan correct?? has first been removed??
            if (only != null) { refinementNodes.add(only);}
            //System.err.println(refinementNodes.toString());
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the concreteAction is high level, get the refinements from the concreteAction
            //System.err.println("Next concreteAction is High Level, thus a we seek refinements to it:");
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refs = highLevelAction.getRefinements(this.getState());
            //System.err.println(refs.toString());

            for (MixedPlan refinement : refs) {

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
        return refinementNodes;
    }

    private HTNNode childNode(ConcreteAction primitiveConcreteAction, MixedPlan remainingActions) {
        HTNState oldState = this.getState();
        HTNState newState = (primitiveConcreteAction ==null) ? oldState : primitiveConcreteAction.applyTo(oldState);
        primitiveConcreteAction = (primitiveConcreteAction ==null) ? new NoConcreteAction() : primitiveConcreteAction;
        //System.err.println("RemActions: " + remainingActions.toString());
        return new HTNNode(this, primitiveConcreteAction, newState, remainingActions);
    }

    public PrimitivePlan extractPlan() {
        LinkedList<ConcreteAction> plan = new LinkedList<>();
        HTNNode node = this;
        ConcreteAction previous;
        while (!node.isInitialNode()) {
            previous = node.getConcreteAction();
            if ((previous != null) && !(previous instanceof NoConcreteAction)) plan.addFirst(previous);
            node = node.getParent();
        }
        return new PrimitivePlan(plan);
    }

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