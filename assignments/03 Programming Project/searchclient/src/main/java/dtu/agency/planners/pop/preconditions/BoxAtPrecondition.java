package dtu.agency.planners.pop.preconditions;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Position;

public class BoxAtPrecondition extends Precondition {

    private Box box;
    private Agent agent;
    private Position boxPosition;

    public BoxAtPrecondition(Box box, Agent agent, Position boxPosition) {
        this.box = box;
        this.agent = agent;
        this.boxPosition = boxPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public Box getBox() {
        return box;
    }

    public Agent getAgent() {
        return agent;
    }
}