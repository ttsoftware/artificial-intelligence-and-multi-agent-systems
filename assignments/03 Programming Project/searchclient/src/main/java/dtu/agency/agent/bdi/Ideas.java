package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.actioncomparators.SolveGoalActionComparator;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;

import java.util.PriorityQueue;

public class Ideas extends Desire<SolveGoalAction> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<SolveGoalAction> ideas;
    private Position agentOrigin;

    public Ideas(Goal goal, Position agentOrigin) {
        super(goal);
        this.agentOrigin = agentOrigin;
        ideas = new PriorityQueue<>(new SolveGoalActionComparator(agentOrigin));
    }

    public Ideas(Ideas ideas) {
        super(ideas.getGoal());
        this.ideas = new PriorityQueue<>( new SolveGoalActionComparator( ideas.getAgentOrigin() ) );
    }

    public PriorityQueue<SolveGoalAction> getIdeas() {
        return ideas;
    }

    public Position getAgentOrigin() {
        return agentOrigin;
    }

    @Override
    public SolveGoalAction getBest() {
        return ideas.poll();
    }
    public SolveGoalAction peekBest() {
        return ideas.peek();
    }

    public void add(SolveGoalAction action) {
        ideas.add(action);
//        if (action !=null) {
//            ideas.add(action);
//        } else {
//            System.err.println("Null SolveGoalAction :-(");
//        }
    }

    @Override
    public String toString() {
        return "Target: " + getGoal().getLabel() + ",Ideas: " + ideas.toString();
    }
}
