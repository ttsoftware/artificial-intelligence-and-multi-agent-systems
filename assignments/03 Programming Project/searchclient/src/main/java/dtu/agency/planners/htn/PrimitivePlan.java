package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.planners.ConcretePlan;
import sun.awt.image.ImageWatched;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class PrimitivePlan implements ConcretePlan {

    private LinkedList<ConcreteAction> concreteActions;

    public PrimitivePlan() {
        concreteActions = new LinkedList<>();
    }

    public Stack<ConcreteAction> getActions() {
        Iterator list = concreteActions.descendingIterator();
        Stack<ConcreteAction> actions = new Stack<>();

        while (list.hasNext()) {
            ConcreteAction nextAction = (ConcreteAction) list.next();
            actions.push(nextAction);
        }
        return actions;
    }

    public LinkedList<ConcreteAction> getActionList() {
        return new LinkedList<>(concreteActions);
    }

    public void pushAction( ConcreteAction action) {
        concreteActions.addFirst(action);
    }
    public void addAction( ConcreteAction action) {
        concreteActions.addLast(action);
    }

    public boolean isEmpty() { return this.concreteActions.isEmpty(); }
    public int size() { return this.concreteActions.size(); }

    @Override
    public String toString() {
        return "PrimitivePlan:"+ getActions().toString();
    }

    public void appendActions(PrimitivePlan other) {
        concreteActions.addAll(other.getActionList());
    }
}
