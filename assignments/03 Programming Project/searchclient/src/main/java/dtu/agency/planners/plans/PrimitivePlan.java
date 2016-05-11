package dtu.agency.planners.plans;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.ConcreteActionType;
import dtu.agency.board.Position;

import java.util.Iterator;
import java.util.LinkedList;

public class PrimitivePlan implements ConcretePlan {

    private LinkedList<ConcreteAction> concreteActions;

    public PrimitivePlan() {
        concreteActions = new LinkedList<>();
    }

    public PrimitivePlan(PrimitivePlan other) {
        this.concreteActions = new LinkedList<>(other.concreteActions);
    }

    public LinkedList<ConcreteAction> getActions() {
        return concreteActions;
    }

    public LinkedList<ConcreteAction> getActionsClone() {
        return new LinkedList<>(concreteActions);
    }

    public ConcreteAction popAction() {
        return concreteActions.pollFirst();
    }

    public void removeLast() {
        concreteActions.removeLast();
    }

    @Override
    public int approximateSteps(Position agentInitialPosition) {
        return concreteActions.size();
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
        concreteActions.addAll(other.getActionsClone());
    }

    /**
     * Removes the last part of the plan, where the agent tries to move back into the box' position
     * @return
     */
    public PrimitivePlan removeGoBack() {

        PrimitivePlan newPlan = new PrimitivePlan(this);

        Iterator<ConcreteAction> actionIterator = this.getActions().descendingIterator();
        while (actionIterator.hasNext()) {
            ConcreteAction action = actionIterator.next();
            if (action.getType().equals(ConcreteActionType.MOVE)) {
                newPlan.removeLast();
            }
            else {
                // break as soon as we see an action which is not move
                break;
            }
        }

        return newPlan;
    }
}
