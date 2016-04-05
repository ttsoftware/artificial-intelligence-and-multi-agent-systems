package dtu.agency.actions.concrete.effects;

import dtu.agency.actions.concrete.MoveAction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.services.LevelService;

public class MoveAgentEffect extends Effect {

    public Level applyChangeToLevel(Level level, MoveAction action) {

        Position goalPosition = LevelService.getInstance().getPositionInDirection(action.getAgentPosition(), action.getDirection());
        BoardCell[][] boardState = level.getBoardState();
        BoardObject[][] boardObjects = level.getBoardObjects();

        level.setBoardState(applyEffectToBoard(level, goalPosition, action));

        return level;
    }

    private BoardCell[][] applyEffectToBoard(Level level, Position goalPosition, MoveAction action) {
        BoardCell[][] boardState = level.getBoardState();
        BoardObject[][] boardObjects = level.getBoardObjects();

        switch (boardState[goalPosition.getRow()][goalPosition.getColumn()]) {
            case AGENT_GOAL:
                boardState[goalPosition.getRow()][goalPosition.getColumn()] = BoardCell.GOAL;

                // We want multiple (agent+goal or box+goal) objects in the same cell
                // boardObjects[goalPosition.getRow()][goalPosition.getColumn()] = new Goal
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
}
