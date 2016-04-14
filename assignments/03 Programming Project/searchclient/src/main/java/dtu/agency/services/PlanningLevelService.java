package dtu.agency.services;

import dtu.agency.board.Level;
import dtu.agency.board.Position;

public class PlanningLevelService extends LevelService {

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
}
