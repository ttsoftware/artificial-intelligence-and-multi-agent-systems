package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

public class ManhattanHeuristic implements Heuristic {

    @Override
    public int distance(Position positionA, Position positionB) {
        return LevelService.getInstance().manhattanDistance(positionA, positionB);
    }
}
