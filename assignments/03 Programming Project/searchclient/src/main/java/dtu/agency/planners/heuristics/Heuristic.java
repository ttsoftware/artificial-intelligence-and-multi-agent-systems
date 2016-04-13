package dtu.agency.planners.heuristics;

import dtu.agency.board.BoardObject;
import dtu.agency.board.Position;

public interface Heuristic {
    int distance(BoardObject boardObjectA, BoardObject boardObjectB);
    int distance(Position positionA, Position positionB);
}