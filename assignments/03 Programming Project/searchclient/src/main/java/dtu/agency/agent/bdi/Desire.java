package dtu.agency.agent.bdi;

import dtu.agency.board.Goal;

/**
 * Represents an agents desires (plans of type T)
 */
abstract class Desire<T> {
    Goal goal;
    Desire(Goal goal){ this.goal = goal; }
    abstract T getBest();
    abstract void add(T plan);
}
