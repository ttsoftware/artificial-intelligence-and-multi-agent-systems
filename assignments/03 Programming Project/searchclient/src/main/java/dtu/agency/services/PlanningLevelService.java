package dtu.agency.services;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.board.*;
import dtu.agency.planners.hlplanner.HLEffect;

import java.util.Iterator;
import java.util.LinkedList;

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

    /**
     * Applies the effects of a HLAction!
     * @param newAgentPosition
     * @param targetBox
     * @param newBoxPosition
     */
    public void updateLevelService(Position newAgentPosition, Box targetBox, Position newBoxPosition) {
        removeAgent(agent);
        removeBox(targetBox);
        insertBox(targetBox, newBoxPosition);
        insertAgent(agent, newAgentPosition);
    }


    /**
     * TODO: applyAll(hlplan) + applyAll(PrimitivePlan)
     * TODO: apply(PrimitiveAction)
     * TODO: revertToState(State?) - ONLY IF NEEDED
     */

    /**
     * Applies a high level action
     */
    public void apply(HLAction action){
        debug("pls.apply HLAction");
        HLEffect effect;
        Box targetBox = action.getBox();
        if (targetBox == null) {
            effect = new HLEffect(
                    getPosition(agent),
                    action.getAgentDestination()
            );
        } else {
            effect = new HLEffect(
                    getPosition(agent),
                    action.getAgentDestination(),
                    targetBox,
                    getPosition(targetBox),
                    action.getBoxDestination()
            );
        }

        removeAgent(agent);
        if (targetBox != null) {
            removeBox(targetBox);
            insertBox(targetBox, effect.boxDestination);
        }
        insertAgent(agent, effect.agentDestination);

        appliedEffects.add(effect);
        debug("This effect has been applied: " + effect);
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
