package dtu.agency.events;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;

public abstract class EstimationEvent extends Event implements Comparable<EstimationEvent> {

    protected final BoardObject task;
    protected final Agent agent;
    protected final int steps;

    public EstimationEvent(BoardObject task, Agent agent, int steps) {
        this.task = task;
        this.agent = agent;
        this.steps = steps;
    }

    public BoardObject getTask() {
        return task;
    }

    public Agent getAgent() {
        return agent;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public int compareTo(EstimationEvent otherEvent) {
        return steps - otherEvent.getSteps();
    }
}
