package dtu.agency.planners.htn;

import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.NoAction;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.actions.effects.HTNEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class HTNNode {

    private static Random rnd = new Random(1);

    private HTNNode parent;
    private Action action;   // primitive action represented by this node
    private HTNEffect effect; // status of the relevant board features
    private MixedPlan remainingActions; // list of successive (abstract) actions
    private int g; // generation - how many ancestors exist? -> how many moves have i performed

    public HTNNode(HTNNode parent, Action action, HTNEffect initialEffects, MixedPlan highLevelPlan) {
        this.parent = parent;
        this.action = action;
        this.effect = initialEffects;
        this.remainingActions = highLevelPlan;
        this.g = (parent == null) ? 0 : (parent.g + 1);
    }

    public int g() {
        return g;
    }

    public boolean isInitialNode() {
        return this.parent == null;
    }

    public ArrayList<HTNNode> getRefinementNodes() {
        System.err.println("HTNNode: getting refinements");

        ArrayList<HTNNode> refinementNodes = new ArrayList<>();

        if (this.remainingActions.isEmpty()) {
            System.err.println("No more remaining actions, returning empty list of refinements");
            return refinementNodes;
        }

        AbstractAction nextAction = remainingActions.removeFirst();

        if (nextAction instanceof Action) { // case the action is primitive, add it as only node,
            System.err.println("Next action is Primitive, thus a single ChildNode is created");
            Action primitive = (Action) nextAction;
            HTNNode only = childNode( primitive, this.remainingActions ); // is remainingActions correct?? has first been removed??
            if (only != null) { refinementNodes.add(only);}
            System.err.println(refinementNodes.toString());
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the action is high level, get the refinements from the action
            System.err.println("Next action is High Level, thus a we seek refinements to it:");
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refs = highLevelAction.getRefinements(this.getEffect());
            System.err.println(refs.toString());

            for (MixedPlan refinement : refs) {

                Action first = null; // remains null iff first action is abstract

                if (refinement.getFirst() instanceof Action) {
                    first = (Action) refinement.removeFirst();
                }
                refinement.extend(remainingActions);

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
        HTNEffect oldState = this.getEffect();
        HTNEffect newState = primitiveAction.applyTo(oldState);
        return new HTNNode(this, primitiveAction, newState, remainingActions);
    }

    public PrimitivePlan extractPlan() {
        LinkedList<Action> plan = new LinkedList<>();
        HTNNode n = this;
        Action previous;
        while (!n.isInitialNode()) {
            previous = n.getAction();
            if ((previous != null) && !(previous instanceof NoAction)) plan.addFirst(previous);
            n = n.getParent();
        }
        return new PrimitivePlan(plan);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parent.hashCode();
        result = prime * result + action.hashCode();
        result = prime * result + effect.hashCode();
        result = prime * result + remainingActions.hashCode();
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
        if (effect != other.effect)
            return false;
        if (!remainingActions.equals(other.remainingActions)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("HTNNode: {");
        s.append("Generation: " + Integer.toString(this.g));
        s.append(", Action: " + ((action!=null) ? action.toString() : "null") );
        s.append(", Effect: " + this.effect.toString() + ",\n");
        s.append("          RemainingActions: " + this.remainingActions.toString() + "}" );
        return s.toString();
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

    public HTNEffect getEffect() {
        return effect;
    }

    public void setEffect(HTNEffect effect) {
        this.effect = effect;
    }
}