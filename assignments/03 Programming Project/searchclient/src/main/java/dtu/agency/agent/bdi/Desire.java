package dtu.agency.agent.bdi;

import dtu.agency.board.Goal;

/**
 * Created by koeus on 4/13/16.
 */
abstract class Desire<T> {
    Goal goal;
    Desire(Goal goal){ this.goal = goal; }
    abstract T getBest();
    abstract void add(T plan);
}
