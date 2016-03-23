package dtu.agency.planners;

import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.AbstractAction;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Searcher {

    private Level level;

    public Searcher(Level level) {
        this.level = level;
    }

    public List<Action> search(AbstractAction action, Agent agent) {
        List<Action> actions = new ArrayList<>();
        Position agentPosition = level.getBoardObjectPositions().get(agent.getLabel());

        while(!this.level.isAdjacent(agentPosition, action.getPosition())) {
            List<Pair<BoardObject, Position>> neighbours = level.getNeighbours(agentPosition);

            int min_h = 32000;
            List<Action> optimalPaths = new ArrayList<>();
            for(Pair<BoardObject, Position> neighbour : neighbours) {
                int h = heuristic(neighbour.getValue(), action.getPosition());
                if (h < min_h) {
                    min_h = h;
                    optimalPaths = new ArrayList<>();
                    MoveAction a = new MoveAction(level.getDirection(agentPosition, neighbour.getValue()));
                    optimalPaths.add(a);
                }
                else if (h == min_h){
                    MoveAction a = new MoveAction(level.getDirection(agentPosition, neighbour.getValue()));
                    optimalPaths.add(a);
                }
            }

            actions.add(optimalPaths.get(0));
        }

        return actions;
    }

    private int heuristic(Position agentPosition, Position goalPosition) {

        return Math.abs(agentPosition.getRow() - goalPosition.getRow()) + Math.abs(agentPosition.getColumn() - goalPosition.getColumn());
    }
}
