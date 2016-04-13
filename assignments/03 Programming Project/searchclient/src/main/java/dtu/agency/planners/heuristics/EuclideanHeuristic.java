package dtu.agency.planners.heuristics;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.services.GlobalLevelService;

public class EuclideanHeuristic implements Heuristic {

    @Override
    public int distance(BoardObject boardObjectA, BoardObject boardObjectB) {
        return GlobalLevelService.getInstance().euclideanDistance(boardObjectA, boardObjectB);
    }

    @Override
    public int distance(Position positionA, Position positionB) {
        return GlobalLevelService.getInstance().euclideanDistance(positionA, positionB);
    }
}
