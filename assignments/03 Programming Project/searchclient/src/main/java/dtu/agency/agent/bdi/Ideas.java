package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.actioncomparators.SolveGoalActionComparator;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;

import java.util.PriorityQueue;

public class Ideas extends Desire<SolveGoalAction> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<SolveGoalAction> ideas;

    public Ideas(Goal goal, PlanningLevelService pls) {
        super(goal);
        ideas = new PriorityQueue<>(new SolveGoalActionComparator(pls));
    }

    public Ideas(Ideas other) {
        super(other.getGoal());
        this.ideas = other.ideas; // TODO: does this even copy??
    }

    public PriorityQueue<SolveGoalAction> getIdeas() {
        return ideas;
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
