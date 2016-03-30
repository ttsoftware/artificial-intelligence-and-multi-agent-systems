package dtu.agency.planners.htn;

import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.AbstractAction;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.actions.MoveBoxAction;
import dtu.agency.services.LevelService;

import java.util.ArrayList;
import java.util.List;

public class HTNPlanner {

    private Agent agent;
    private Goal goal;

    public HTNPlanner(Agent agent, Goal goal) {
        this.agent = agent;
        this.goal = goal;
    }

    /**
     * Split the goal into subgoals
     * @return
     */
    public HTNPlan plan() {
        List<AbstractAction> actions = new ArrayList<>();

        // Find the box closest to this agent
        Box box = LevelService.getInstance().closestBox(agent, goal);

        // Go to this box - estimate distance
        int gotoDistance = LevelService.getInstance().manhattanDistance(agent, box);

        Position boxPosition = LevelService.getInstance().getLevel().getBoardObjectPositions().get(box.getLabel());

        actions.add(new GotoAction(gotoDistance, new Position(boxPosition.getRow(), boxPosition.getColumn())));

        // Move this box to the goal - estimate distance
        int moveDistance = LevelService.getInstance().manhattanDistance(box, goal);

        actions.add(new MoveBoxAction(moveDistance, box, goal));

        return new HTNPlan(actions);
    }
}
