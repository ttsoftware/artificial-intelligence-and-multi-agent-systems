package dtu.agency.planners.htn;

import com.sun.org.apache.xerces.internal.dom.ChildNode;
import dtu.agency.AbstractAction;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.board.Level;
import dtu.agency.planners.AbstractPlan;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.MixedPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.planners.actions.HLAction;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.searchclient.Command;
import dtu.searchclient.Command.dir;
import dtu.searchclient.Command.type;
import javafx.util.Pair;

import java.util.*;

public class HTNNode {

    // REALLY NEEDS SOME ATTENTION - SHOULD BE REVISITED FROM START TO END!
    // Builds on Node from SearchClient, which is quite unlike what this should be like...


    // public static Map<HTNEffect,boolean> visitedEffects = new HashMap<>(); // in strategy instead?!

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

    public ArrayList<HTNNode> getRefinementNodes(Level level) {

        ArrayList<HTNNode> refinementNodes = new ArrayList<>();

        if (this.remainingActions.isEmpty()) {return refinementNodes;}

        AbstractAction nextAction = remainingActions.removeFirst();

        if (nextAction instanceof Action) { // case the action is primitive, add it as only node,
            Action primitive = (Action) nextAction;
            HTNNode only = childNode( primitive, this.remainingActions, level ); // is remainingActions correct?? has first been removed??
            if (only != null) { refinementNodes.add(only);}
            return refinementNodes;
        }

        if (nextAction instanceof HLAction) { // case the action is high level, get the refinements from the action
            HLAction highLevelAction = (HLAction) nextAction;
            ArrayList<MixedPlan> refs = highLevelAction.getRefinements(this.getEffect(), level);
            // go nuts!
            Action first;
            HTNNode nextNode;
            for (MixedPlan refinement : refs) {
                first = null; // remains null iff first action is abstract
                if (refinement.getFirst() instanceof Action) {
                    first = (Action) refinement.removeFirst();
                }
                refinement.extend(remainingActions);
                nextNode = childNode( first, refinement, level );
                if (nextNode != null) {
                    refinementNodes.add(nextNode);
                }
            }

            //get refinements
            // make sure to check if next's subgoal is reached by any of the refinements -- ?? how ?? store subgoal in hlaction??
        }

        Collections.shuffle(refinementNodes, rnd);
        return refinementNodes;
    }

    private HTNNode childNode(Action primitiveAction, MixedPlan remainingActions, Level level) {
        HTNEffect oldState = this.getEffect();
        HTNEffect newState = primitiveAction.applyTo(oldState);

        // maybe postpone this check until HTNPlanner picks the Node??
        // could be moved inside getRefinements, and spare iteration of illegal nodes.
        // Then we will have the level available

        return new HTNNode(this, primitiveAction, newState, remainingActions);
    }

    public PrimitivePlan extractPlan() {
        LinkedList<Action> plan = new LinkedList<>();
        HTNNode n = this;
        Action previous;
        while (!n.isInitialNode()) {
            previous = n.getAction();
            if (previous != null) plan.addFirst(previous);
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
        s.append("HTNNode: \n");
        s.append("Level :");
        s.append(Integer.toString(this.g));
        s.append("\n");
        s.append("Action :");
        s.append(this.action.toString());
        s.append("\n");
        s.append("Effect :");
        s.append(this.effect.toString());
        s.append("\n");
        s.append("RemainingActions :");
        s.append(this.remainingActions.toString());
        s.append("\n");
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