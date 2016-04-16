package dtu.agency.services;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;

public class PlanningLevelService extends LevelService {

    Box currentBox;
    Position currentBoxPosition;
    Position currentAgentPosition;

    public PlanningLevelService(PlanningLevelService other) {
        setLevel(new Level(other.getLevel()));
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = getAgentPosition();
    }

    public PlanningLevelService(Level level) {
        setLevel(level);
        currentBox = null;
        currentBoxPosition = null;
        currentAgentPosition = getAgentPosition();
    }

    private boolean trackingAgent() {
        return (currentAgentPosition != null);
    }

    private boolean trackingBox() {
        return (currentBox != null);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Position getAgentPosition() {
        if (trackingAgent()) {
            return currentAgentPosition;
        } else {
            return getPosition(BDIService.getInstance().getAgent());
        }
    }

    public void setCurrentBox(Box currentBox, Position position) {
        this.currentBox = currentBox;
        this.currentBoxPosition = position;
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public Position getCurrentBoxPosition() {
        return currentBoxPosition;
    }

    @Override
    public Position getPosition(BoardObject obj){
        if (obj.equals(currentBox)) {
            return currentBoxPosition;
        } else {
            return super.getPosition(obj);
        }

    }

    public void setCurrentBoxPosition(Position currentBoxPosition) {
        this.currentBoxPosition = currentBoxPosition;
    }

    public void setCurrentAgentPosition(Position currentAgentPosition) {
        this.currentAgentPosition = currentAgentPosition;
    }

    public void setCurrentBox(Box currentBox) {
        this.currentBox = currentBox;
    }
}
