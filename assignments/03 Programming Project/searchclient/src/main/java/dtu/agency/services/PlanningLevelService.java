package dtu.agency.services;

import dtu.agency.board.*;

public class PlanningLevelService extends LevelService {

    Agent agent = BDIService.getInstance().getAgent();
    Position currentAgentPosition;
    Box currentBox;
    Position currentBoxPosition;

    public PlanningLevelService(PlanningLevelService other) {
        setLevel(new Level(other.getLevel()));
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = null;
    }

    public PlanningLevelService(Level level) {
        setLevel(level);
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = null;
    }

    /**
     * should enable pls to track a box and the agent
     * if so, all levelservice methods should not see the agent and the box
     * excluding getPosition(BoardObj)
     */
    public void startTrackingBox(Box box){
        // remove box from level
        currentBox = box;
        currentBoxPosition = getPosition(box);
        removeBox(box);
    }

    public void stopTrackingBox(){
        // reinsert box into level
        if (trackingBox()) {
            insertBox(currentBox, currentBoxPosition);
            currentBox = null;
            currentBoxPosition = null;
        }
    }

    public void startTrackingAgent(){
        // remove agent from level
        currentAgentPosition = getPosition(agent);
        removeAgent(agent);
    }

    public void stopTrackingAgent(){
        // reinsert agent into level
        if (trackingAgent()) {
            insertAgent(agent, currentAgentPosition);
            currentAgentPosition = null;
        }
    }

    public void updateLevelService(Position newAgentPosition, Box targetBox, Position newBoxPosition) {
        removeAgent(agent);
        removeBox(targetBox);
        insertBox(targetBox, newBoxPosition);
        insertAgent(agent, newAgentPosition);
    }

    public Box getTrackingBox(){
        return currentBox;
    }

    private boolean trackingAgent() {
        return (currentAgentPosition != null);
    }

    private boolean trackingBox() {
        return ((currentBox != null) && (currentBoxPosition != null));
    }

    /**
     * THIS SHOULD REPLACE getAgentPosition, set/getcurrentboxposition()
     */



    public void setLevel(Level level) {
        this.level = level;
    }

//    public void setCurrentBox(Box currentBox, Position position) {
//        this.currentBox = currentBox;
//        this.currentBoxPosition = position;
//    }
//
//    public Box getCurrentBox() {
//        return currentBox;
//    }
//
//    public Position getCurrentBoxPosition() {
//        return currentBoxPosition;
//    }

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

//    public void setCurrentBoxPosition(Position currentBoxPosition) {
//        this.currentBoxPosition = currentBoxPosition;
//    }
//
//    public void setCurrentAgentPosition(Position currentAgentPosition) {
//        this.currentAgentPosition = currentAgentPosition;
//    }
//
//    public void setCurrentBox(Box currentBox) {
//        this.currentBox = currentBox;
//    }
}
