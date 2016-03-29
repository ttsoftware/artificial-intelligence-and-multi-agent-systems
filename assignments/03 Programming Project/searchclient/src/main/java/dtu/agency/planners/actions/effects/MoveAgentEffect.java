package dtu.agency.planners.actions.effects;

import dtu.agency.BoardObjectHelper;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Level;
import dtu.agency.board.Position;

public class MoveAgentEffect extends Effect {

    public Level applyChangeToLevel(Level level, MoveAction action) {
        BoardObjectHelper boardObjectHelper = new BoardObjectHelper(level.getBoardObjects(), level.getBoardState());

        Position goalPosition = boardObjectHelper.getPositionInDirection(action.getAgentPosition(), action.getDirection());
        BoardCell[][] boardState = level.getBoardState();
        BoardObject[][] boardObjects = level.getBoardObjects();

        level.setBoardState(applyEffectToBoardState(boardState, goalPosition, action));
        level.setBoardObjects(applyEffectToBoardObjects(boardObjects, goalPosition, action));

        return level;
    }

    private BoardCell[][] applyEffectToBoardState(BoardCell[][] boardState, Position goalPosition, MoveAction action) {
        switch (boardState[goalPosition.getRow()][goalPosition.getColumn()]) {
            case AGENT_GOAL:
                boardState[goalPosition.getRow()][goalPosition.getColumn()] = BoardCell.GOAL;
                break;
            default:
                boardState[goalPosition.getRow()][goalPosition.getColumn()] = BoardCell.FREE_CELL;
        }

        switch (boardState[action.getAgentPosition().getRow()][action.getAgentPosition().getColumn()]) {
            case GOAL:
                boardState[action.getAgentPosition().getRow()][action.getAgentPosition().getColumn()] = BoardCell.AGENT_GOAL;
                break;
            default:
                boardState[action.getAgentPosition().getRow()][action.getAgentPosition().getColumn()] = BoardCell.AGENT;
        }

        return boardState;
    }

    private BoardObject[][] applyEffectToBoardObjects(BoardObject[][] boardObjects, Position goalPosition, MoveAction action) {


        return boardObjects;
    }
}
