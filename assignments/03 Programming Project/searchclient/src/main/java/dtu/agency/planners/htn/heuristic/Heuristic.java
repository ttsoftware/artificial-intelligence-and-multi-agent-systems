package dtu.agency.planners.htn.heuristic;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;

public interface Heuristic {
    int distance(BoardObject boardObjectA, BoardObject boardObjectB);
    int distance(Position positionA, Position positionB);
}