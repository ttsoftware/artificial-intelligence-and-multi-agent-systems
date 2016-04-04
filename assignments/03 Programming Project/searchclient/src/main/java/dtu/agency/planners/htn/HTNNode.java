package dtu.agency.planners.htn;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.NoAction;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.HLAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class HTNNode {

    private static Random rnd = new Random(1);

    private HTNNode parent;
    private Action action;   // primitive action represented by this node
    private HTNState state;  // status of the relevant board features after applying the action of this node
    private MixedPlan remainingPlan; // list of successive (abstract) actions
    private int g; // generation - how many ancestors exist? -> how many moves have i performed

    public HTNNode(HTNNode parent, Action action, HTNState initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.action = action;
        this.state = initialEffects;
        this.remainingPlan = highLevelPlan;
        this.g = (parent == null) ? 0 : (parent.g + 1);
    }

    public int g() {
        return g;
    }

    public boolean isInitialNode() {
        return this.parent == null;
    }

    public HTNNode getParent() {
        return parent;
    }

    public void setParent(HTNNode parent) {
        this.parent = parent;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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

        AbstractAction nextAction = remainingPlan.removeFirst();

        if (nextAction instanceof Action) { // case the action is primitive, add it as only node,
            //System.err.println("Next action is Primitive, thus a single ChildNode is created");
            Action primitive = (Action) nextAction;
            HTNNode only = childNode( primitive, this.remainingPlan); // is remainingPlan correct?? has first been removed??
            if (only != null) { refinementNodes.add(only);}
            //System.err.println(refinementNodes.toString());
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the action is high level, get the refinements from the action
            //System.err.println("Next action is High Level, thus a we seek refinements to it:");
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refs = highLevelAction.getRefinements(this.getState());
            //System.err.println(refs.toString());

            for (MixedPlan refinement : refs) {

                Action first = null; // remains null iff first action is abstract

                if (refinement.getFirst() instanceof Action) {
                    first = (Action) refinement.removeFirst();
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

    private HTNNode childNode(Action primitiveAction, MixedPlan remainingActions) {
        HTNState oldState = this.getState();
        HTNState newState = primitiveAction.applyTo(oldState);
        return new HTNNode(this, primitiveAction, newState, remainingActions);
    }

    public PrimitivePlan extractPlan() {
        LinkedList<Action> plan = new LinkedList<>();
        HTNNode node = this;
        Action previous;
        while (!node.isInitialNode()) {
            previous = node.getAction();
            if ((previous != null) && !(previous instanceof NoAction)) plan.addFirst(previous);
            node = node.getParent();
        }
        return new PrimitivePlan(plan);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parent.hashCode();
        result = prime * result + action.hashCode();
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
        if (action != other.action)
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
        StringBuilder s = new StringBuilder();
        s.append("HTNNode: {");
        s.append("Generation: " + Integer.toString(this.g));
        s.append(", Action: " + ((action!=null) ? action.toString() : "null") );
        s.append(", Effect: " + this.state.toString() + ",\n");
        s.append("          RemainingActions: " + this.remainingPlan.toString() + "}" );
        return s.toString();
    }

}