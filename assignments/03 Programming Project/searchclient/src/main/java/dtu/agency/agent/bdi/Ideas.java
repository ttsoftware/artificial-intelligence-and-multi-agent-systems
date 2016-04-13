package dtu.agency.agent.bdi;

import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.HLActionComparator;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;

import java.util.PriorityQueue;

public class Ideas extends Desire<HLAction> { // everything the agent might want to achieve

    // should one know the initial position of this plan??
    private PriorityQueue<HLAction> ideas;

    public Ideas(Goal goal, Position agentOrigin) {
        super(goal);
        ideas = new PriorityQueue<>(new HLActionComparator(agentOrigin));
    }

    @Override
    public HLAction getBest() {
        return ideas.poll();
    }

    public void add(HLAction action) {
        if (action !=null) {
            ideas.add(action);
        }
    }

    @Override
    public String toString() {
        return "Target: " + goal.getLabel() + ",Ideas: " + ideas.toString();
    }
}
