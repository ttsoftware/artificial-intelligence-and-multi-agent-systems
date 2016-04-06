package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

public class EuclideanHeuristic implements Heuristic {

    @Override
    public int distance(BoardObject boardObjectA, BoardObject boardObjectB) {
        return LevelService.getInstance().euclideanDistance(boardObjectA, boardObjectB);
    }

    @Override
    public int distance(Position positionA, Position positionB) {
        return LevelService.getInstance().euclideanDistance(positionA, positionB);
    }
}
