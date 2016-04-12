package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.services.GlobalLevelService;

public class ManhattanHeuristic implements Heuristic {

    @Override
    public int distance(BoardObject boardObjectA, BoardObject boardObjectB) {
        return GlobalLevelService.getInstance().manhattanDistance(boardObjectA, boardObjectB);
    }

    @Override
    public int distance(Position positionA, Position positionB) {
        return GlobalLevelService.getInstance().manhattanDistance(positionA, positionB);
    }
}
