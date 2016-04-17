package dtu.agency.services;

import dtu.agency.actions.Action;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.hlplanner.HLEffect;
import dtu.agency.planners.plans.Plan;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PlanningLevelService extends LevelService {

    Agent agent = BDIService.getInstance().getAgent(); // The agent planning this
    Position currentAgentPosition;                     // if tracking, the agent's position
    Box currentBox;                                    // if tracking, the box tracked
    Position currentBoxPosition;                       // if tracking, the box' position
    LinkedList<HLEffect> appliedEffects;               // stored for reverse application purpose


    public PlanningLevelService(PlanningLevelService other) {
        setLevel(new Level(other.getLevel()));
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = null;
        appliedEffects = new LinkedList<>();
    }

    public PlanningLevelService(Level level) {
        setLevel(level);
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = null;
        appliedEffects = new LinkedList<>();
    }

    public Box getTrackingBox(){
        return currentBox;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

//    /**
//     * Applies the effects of a HLAction!
//     * @param newAgentPosition
//     * @param targetBox
//     * @param newBoxPosition
//     */
//    public void updateLevelService(Position newAgentPosition, Box targetBox, Position newBoxPosition) {
//        removeAgent(agent);
//        removeBox(targetBox);
//        insertBox(targetBox, newBoxPosition);
//        insertAgent(agent, newAgentPosition);
//    }

    /**
     * Applies any applicable action - high level, recursive level and concrete actions
     * SolveGoalActions are not applicable in order to get a consistent / precise state in the end
     * (but might be applicable in order to guess on the sequence in which order to solve the goals!)
     */
    public void apply(Action action){
        debug("pls.apply Action: " + action);
        HLEffect effect = null;
        Box box = null;

        if (action instanceof HLAction) {
            HLAction hlAction = (HLAction) action;
            box = hlAction.getBox();

            // record effect
            if (box == null) {
                effect = new HLEffect(
                        getPosition(agent),
                        hlAction.getAgentDestination()
                );
            } else {
                effect = new HLEffect(
                        getPosition(agent),
                        hlAction.getAgentDestination(),
                        box,
                        getPosition(box),
                        hlAction.getBoxDestination()
                );
            }

            // update level
            removeAgent(agent);
            if (box != null) {
                removeBox(box);
                insertBox(box, effect.boxDestination);
            }
            insertAgent(agent, effect.agentDestination);

        } else if (action instanceof ConcreteAction) {
            ConcreteAction cAction = (ConcreteAction) action;
            Position agentOrigin = getPosition(agent);

            switch (cAction.getType()) {
                case MOVE:
                    MoveConcreteAction moveAction = (MoveConcreteAction) cAction;
                    effect = new HLEffect(
                            agentOrigin,
                            getAdjacentPositionInDirection(agentOrigin, moveAction.getAgentDirection())
                    );
                    move(agent, moveAction);
                    break;

                case PUSH:
                    PushConcreteAction pushAction = (PushConcreteAction) cAction;
                    // record effect
                    effect = new HLEffect(
                            agentOrigin,
                            getAdjacentPositionInDirection(agentOrigin, pushAction.getAgentDirection()),
                            pushAction.getBox(),
                            getPosition(pushAction.getBox()),
                            getAdjacentPositionInDirection(getPosition(pushAction.getBox()), pushAction.getBoxMovingDirection())
                    );

                    // update level move objects - Important with the order of operations
                    push(agent, pushAction);
                    break;

                case PULL:
                    PullConcreteAction pullAction = (PullConcreteAction) cAction;
                    // record effect
                    effect = new HLEffect(
                            agentOrigin,
                            getAdjacentPositionInDirection(agentOrigin, pullAction.getAgentDirection()),
                            pullAction.getBox(),
                            getPosition(pullAction.getBox()),
                            getAdjacentPositionInDirection(getPosition(pullAction.getBox()), pullAction.getBoxMovingDirection())
                    );

                    // update level move objects - Important with the order of operations
                    pull(agent, pullAction);
                    break;

                case NONE:
                    debug("None type action does not change state");
                    return;
            }

        } else {
            throw new AssertionError("Not an applicable action: " + action);
        }

        // update applied effects variable
        appliedEffects.add(effect);
        debug("This effect has been applied: " + effect);

    }

    /**
     * Applies all actions in a plan
     * @param plan any plan implementing the Plan interface
     */
    public void applyAll(Plan plan){
        List<Action> allActions = plan.getActions();
        Iterator actions = allActions.iterator();
        while (actions.hasNext()) {
            Action next = (Action) actions.next();
            apply(next);
        }
    }

    public void revertLast(){
        debug("pls.revertLast");
        HLEffect last = appliedEffects.removeLast();
        assert (getPosition(agent)==last.agentDestination);
        removeAgent(agent);
        if (last.box != null) {
            assert (getPosition(last.box)==last.boxDestination);
            removeBox(last.box);
            insertBox(last.box, last.boxOrigin);
        }
        insertAgent(agent, last.agentOrigin);
        debug("This effect has been applied in reverse: " + last);
    }

    public void revertAll(){
        debug("pls.revertAll()");
        while (appliedEffects.size() > 0) {
            revertLast();
        }
        debug("All tracked effects are reversed");
    }

    /**
     * should enable pls to 'track' a box and the agent
     * meaning that it is removed from the level when tracking starts
     * and reinserted when tracking stops
     */
    public void startTracking(Box box) {
        startTrackingAgent();
        if (box != null){
            startTrackingBox(box);
        }
    }

    public void stopTracking() {
        if (trackingBox()) {
            stopTrackingBox();
        }
        stopTrackingAgent();
    }

    private void startTrackingBox(Box box){
        // remove box from level
        currentBox = box;
        currentBoxPosition = getPosition(box);
        removeBox(box);
    }
    private void startTrackingAgent(){
        // remove agent from level
        currentAgentPosition = getPosition(agent);
        removeAgent(agent);
    }

    private void stopTrackingBox(){
        // reinsert box into level
        if (trackingBox()) {
            insertBox(currentBox, currentBoxPosition);
            currentBox = null;
            currentBoxPosition = null;
        }
    }

    private void stopTrackingAgent(){
        // reinsert agent into level
        if (trackingAgent()) {
            insertAgent(agent, currentAgentPosition);
            currentAgentPosition = null;
        }
    }


    private boolean trackingAgent() {
        return (currentAgentPosition != null);
    }

    private boolean trackingBox() {
        return ((currentBox != null) && (currentBoxPosition != null));
    }


    @Override
    public Position getPosition(BoardObject obj){
        if ( trackingBox() && obj.equals(currentBox) ) {
            return currentBoxPosition;
        } else if ( trackingAgent() && obj.equals(BDIService.getInstance().getAgent())) {
            return currentAgentPosition;
        } else {
            return super.getPosition(obj);
        }

    }

}
