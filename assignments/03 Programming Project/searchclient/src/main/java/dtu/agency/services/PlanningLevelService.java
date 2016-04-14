package dtu.agency.services;

import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;

public class PlanningLevelService extends LevelService {

    Box currentBox;
//    Position currentBoxPosition;

    public PlanningLevelService(PlanningLevelService other) {
        setLevel(new Level(other.getLevel()));
    }

    public PlanningLevelService(Level level) {
        setLevel(level);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Position getAgentPosition() {
        return getPosition(BDIService.getInstance().getAgent());
    }

    public void setCurrentBox(Box currentBox) {
        this.currentBox = currentBox;
//        this.currentBoxPosition = getPosition(currentBox);
    }

    public Box getCurrentBox() {
        return currentBox;
    }

//    public Position getCurrentBoxPosition() {
//        return currentBoxPosition;
//    }

//    public void setCurrentBoxPosition(Position currentBoxPosition) {
//        this.currentBoxPosition = currentBoxPosition;
//    }
}
