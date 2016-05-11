package dtu.agency.conflicts;

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.GlobalLevelService;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by rasmus on 5/11/16.
 */
public class ConflictResolutionTest {

    private static File resourcesDirectory = new File("src/test/resources");

    @Test
    public void swapConflictTest() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/MAconflicts_simple3.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

        List<Agent> agents = GlobalLevelService.getInstance().getLevel().getAgents();
        Agent agent1 = agents.get(0);
        PrimitivePlan agent1Plan = new PrimitivePlan();
        agent1Plan.addAction(new MoveConcreteAction(Direction.EAST));

        Agent agent2 = agents.get(1);
        PrimitivePlan agent2Plan = new PrimitivePlan();
        agent2Plan.addAction(new MoveConcreteAction(Direction.WEST));
        agent2Plan.addAction(new MoveConcreteAction(Direction.NORTH));

        Conflict conflict = new Conflict(
                agent1,
                agent1Plan,
                agent2,
                agent2Plan
        );

        assertTrue(conflict.getInitiator().equals(agent1));
        assertTrue(conflict.getInitiatorPlan().getActions().get(0).getAgentDirection().equals(Direction.EAST));
        assertTrue(conflict.getInitiatorPosition().equals(new Position(1, 4)));

        assertTrue(conflict.getConceder().equals(agent2));
        assertTrue(conflict.getConcederPlan().getActions().get(0).getAgentDirection().equals(Direction.WEST));
        assertTrue(conflict.getConcederPlan().getActions().get(1).getAgentDirection().equals(Direction.NORTH));
        assertTrue(conflict.getConcederPosition().equals(new Position(1, 7)));

        conflict.swap();

        assertTrue(conflict.getInitiator().equals(agent2));
        assertTrue(conflict.getInitiatorPlan().getActions().get(0).getAgentDirection().equals(Direction.WEST));
        assertTrue(conflict.getInitiatorPlan().getActions().get(1).getAgentDirection().equals(Direction.NORTH));
        assertTrue(conflict.getInitiatorPosition().equals(new Position(1, 7)));

        assertTrue(conflict.getConceder().equals(agent1));
        assertTrue(conflict.getConcederPlan().getActions().get(0).getAgentDirection().equals(Direction.EAST));
        assertTrue(conflict.getConcederPosition().equals(new Position(1, 4)));
    }

    @Test
    public void conflictEqualsTest() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/MAconflicts_simple3.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

        List<Agent> agents = GlobalLevelService.getInstance().getLevel().getAgents();
        Agent agent1 = agents.get(0);
        PrimitivePlan agent1Plan = new PrimitivePlan();
        agent1Plan.addAction(new MoveConcreteAction(Direction.EAST));

        Agent agent2 = agents.get(1);
        PrimitivePlan agent2Plan = new PrimitivePlan();
        agent2Plan.addAction(new MoveConcreteAction(Direction.WEST));
        agent2Plan.addAction(new MoveConcreteAction(Direction.NORTH));

        ResolvedConflict resolvedConflict1 = new ResolvedConflict(
                agent1,
                agent1Plan,
                GlobalLevelService.getInstance().getPosition(agent1),
                agent2,
                agent2Plan,
                GlobalLevelService.getInstance().getPosition(agent2)
        );

        ResolvedConflict resolvedConflict2 = new ResolvedConflict(
                agent1,
                agent1Plan,
                GlobalLevelService.getInstance().getPosition(agent1),
                agent2,
                agent2Plan,
                GlobalLevelService.getInstance().getPosition(agent2)
        );

        ResolvedConflict resolvedConflict3 = new ResolvedConflict(
                agent2,
                agent1Plan,
                GlobalLevelService.getInstance().getPosition(agent1),
                agent1,
                agent2Plan,
                GlobalLevelService.getInstance().getPosition(agent2)
        );

        assertTrue(resolvedConflict1.equals(resolvedConflict2));
        assertTrue(!resolvedConflict1.equals(resolvedConflict3));
    }
}
